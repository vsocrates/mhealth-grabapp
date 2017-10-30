require "torch"
require "nn"
require "math"


-- global variables
learningRate = 0.01
innerIteration = 1
outerIteration = 1
fraction = 0.01
batchsize = 500
num_roc_points = 10

function subset(dataset, head, tail)
  local sub = {}
  local index = 0
  for i = head, tail do
    index = index + 1
    sub[index] = dataset[i]
  end
  function sub:size() return index end

  return sub
end

-- --ANN with 5 hidden layer
function create_network()

  local ann = nn.Sequential(); 

  ann:add(nn.View(561 * 1))
  ann:add(nn.Linear(561, 375))
  ann:add(nn.ReLU())

  ann:add(nn.Linear(375, 350))
  ann:add(nn.ReLU())

  ann:add(nn.Linear(350, 325))
  ann:add(nn.ReLU())

  ann:add(nn.Linear(325, 300))
  ann:add(nn.ReLU())

  ann:add(nn.Linear(300, 100))
  ann:add(nn.ReLU())

  --output layer
  ann:add(nn.Linear(100, 12))
  ann:add(nn.ReLU())
  ann:add(nn.LogSoftMax())

  return ann
end


-- here we set up the architecture of the neural network
-- input raw data 561 * 1
--ANN with two hidden layer
-- function create_network()

--   local ann = nn.Sequential(); 

--   ann:add(nn.View(561 * 1))
--   ann:add(nn.Linear(561, 300))
--   ann:add(nn.ReLU())

--   ann:add(nn.Linear(300, 100))
--   ann:add(nn.ReLU())

--   --output layer
--   ann:add(nn.Linear(100, 12))
--   ann:add(nn.ReLU())
--   ann:add(nn.LogSoftMax())

--   return ann
-- end

--[[
--ANN with no hidden layer
-- input raw data 561 * 1
function create_network()

  local ann = nn.Sequential(); 

  --output layer
  ann:add(nn.View(561 * 1))
  ann:add(nn.Linear(561, 12))
  ann:add(nn.ReLU())
  ann:add(nn.LogSoftMax())

  return ann
end
--]]

--[[
--ANN with one hidden layer
-- input raw data 561 * 1
function create_network()

  local ann = nn.Sequential(); 

  --output layer
  ann:add(nn.View(561 * 1))
  ann:add(nn.Linear(561, 100))
  ann:add(nn.ReLU())

  ann:add(nn.Linear(100, 12))
  ann:add(nn.ReLU())
  ann:add(nn.LogSoftMax())

  return ann
end
--]]
 
--In this function, we update the parameters of network with current_batch.
function update(network, dataset)
  local criterion = nn.ClassNLLCriterion()
  for iteration = 1, innerIteration do
    -- print("quux this is inner iteration number " .. iteration)
    for t = 1, table.getn(dataset) do
      local example = dataset[t]
      local input = example[1]
      local target = example[2]
      
      --zero the buffer of gradients.
      network:zeroGradParameters();
      --feedforward and backpropagation.
      network:backward(input, criterion:backward(network:forward(input), target));
      --update the weights using trained gradients
      network:updateParameters(learningRate);
    end
    --test current model.
    test_predictor(network, testing_dataset, classes, classes_names)
  end
end

--You should modify test_predictor() to get multiple confusion matrices. 
--print() what you want to observe.
function test_predictor(predictor, test_dataset, classes, classes_names)

  local accuracy_precision_recall_falsepositiverate = {}

  local mistakes = 0
  local true_positives = 0
  local false_positives = 0
  local true_negatives = 0
  local false_negatives = 0
  local actual_positives = 0
  local actual_negatives = 0
  local tested_samples = 0
  local thresholds = {}
  --print("the length of dataset is ", table.getn(test_dataset))
  for j = 1, tonumber(num_roc_points) do
    thresholds[j] = {j*.1, 1-(j *.1)}
  end
  thresholds[num_roc_points] = {0.95, .05}
  --print(thresholds)
  for m =1, tonumber(num_roc_points) do
    for i = 1, tonumber(table.getn(test_dataset)) do
      local input = test_dataset[i][1]
      local class_id = test_dataset[i][2]
      local responses_per_class = predictor:forward(input)
      local probabilites_per_class = torch.exp(responses_per_class)
      --print(responses_per_class)

     local class_A_probability, prediction = torch.max(probabilites_per_class, 1)
      local not_max_class_score = 0 
      for j=1, 12 do
        if j ~= prediction then
          not_max_class_score = not_max_class_score + tonumber(probabilites_per_class[j])
        end
      end
      not_max_class_score = not_max_class_score/11

      --print("not max score", not_max_class_score)


      --Compare the confidence score of all the classes, 
      --the one with maximum score will be assigned to current test sample.
      --To draw ROC curve, you can define the misclassification cost vector with entries:
      --C_{ij}= relative cost of misclassifying a data point in class j to class i.
      --For instance, classifying a cancerous biopsy as benign is more
      --critical than classifying a benign biopsy as malignant.
      --If you are most intersted in class A, you can average confidence score of all the remaining classes and
      --treat all the remaining classes as one class, namely, not A. 
      --Then, give misclassification cost to "A" and "not A". For example, if the confidence score of "A" and "not A"
      --is [5,10] and the misclassification cost of "A" and "not A" is [0.8,0.2], then the true score of "A" is 5*0.8=4 and
      --the true score of "not A" is 10*0.2=2. Therefore, the current test sample belongs to "A".
      --By changing the misclassification cost vector, you get multiple confusion matrices, then you get multiple points on ROC space.
      --In our case, you should fix the weight for "not A", change the weight for "A" from small to large. 
      local probability, prediction = torch.max(probabilites_per_class, 1)

      local temp_predicted_value = 0

      local temp_predicted_value_1 = thresholds[m][1] * class_A_probability[1]
      local temp_predicted_value_2 = thresholds[m][2] * not_max_class_score
      if temp_predicted_value_1 > temp_predicted_value_2 then
        temp_predicted_value = 1
      else
        temp_predicted_value = 0
      end

      local label = class_id[1]
      local predicted_label = prediction[1]
      local temp_label_value = 0
      --local temp_predicted_value = 0
      if tonumber(label) == 4 then
        temp_label_value = 1
      else
        temp_label_value = 0
      end
      -- if tonumber(predicted_label) == 4 then
      --   temp_predicted_value = 1
      -- else
      --   temp_predicted_value = 0
      -- end
      --print("printing temp label ", temp_label_value)
      --print("printing temp predicted ", temp_predicted_value)

      if temp_label_value ~= temp_predicted_value then
        --print("they are not equal")
        mistakes = mistakes + 1
        if temp_label_value == 1 then
            false_negatives = false_negatives + 1 
            actual_positives = actual_positives + 1
        else 
          false_positives = false_positives + 1
          actual_negatives = actual_negatives + 1
        end
        --print(i , label , predicted_label )
      else
        if temp_label_value == 1 then
          true_positives = true_positives + 1
          actual_positives = actual_positives + 1
        else
          true_negatives = true_negatives + 1
          actual_negatives = actual_negatives + 1
        end
      end
      tested_samples = tested_samples + 1
    end

    local test_err = mistakes / tested_samples
    local accuracy = 1 - test_err
    local true_positive_rate = true_positives/actual_positives
    local true_negative_rate = true_negatives/actual_negatives
    local false_positive_rate = false_positives/actual_negatives
    local precision = true_positives/(false_positives + true_positives)

    --local file = io.write("file.txt", "w")
    --file.write()
    accuracy_precision_recall_falsepositiverate[m] = {accuracy, precision ,true_positive_rate, false_positive_rate}



    -- print("accuracy = ",accuracy)
    --print("true positive rate = ",true_positive_rate)
    --print("false positive rate = ", false_positive_rate )
    --print("specificity = ", true_negative_rate)
    --print("the mistakes are ", mistakes)
    --print("the true negatives are ", true_negatives)
    --print("the false_negatives are ", false_negatives)
    --print("the actual positives are ", actual_positives)
    --print("the actual negatives are ", actual_negatives)
  end
  return accuracy_precision_recall_falsepositiverate
end

function train(network)
  for index=0,batch_number do
    local dataIndex = index
    -- print("batch number:" .. dataIndex)
    -- print("---start training---!")
    local startPoint = 1+dataIndex*batchsize
    local endPoint = dataIndex*batchsize+batchsize
    --If endpoint is not exceed the size of the training set,
    --we update the model using current batch. 
    if endPoint<train_size then
      local current_batch = subset(training_dataset, startPoint, endPoint)
      ---start training---
      ----------------------------------------------
      update(network, current_batch)
      -- print("---start testing---!")
      test_predictor(network, testing_dataset, classes, classes_names)
      -- print("Finish Training!")
    --If endpoint is exceed the size of the training set but startPoint is not exceed,
    --we also update the model using current batch.
    elseif startPoint<train_size then
      local current_batch = subset(training_dataset, startPoint, train_size)
      ---start training---
      ----------------------------------------------
      update(network, current_batch)
      -- print("---start testing---!")
      test_predictor(network, testing_dataset, classes, classes_names)
      -- print("Finish Training!")
    --Done.
    else
      -- print('I am done.')
    end
  end
end

-- main routine
function main()
  cmd = torch.CmdLine()
  
  cmd:text()
  cmd:text()
  cmd:text('Training a simple network')
  cmd:text()
  cmd:text('Options')
  cmd:option('-RunFunction',0,'0 is cross validation, 1 is algorithm on the full sample')
  cmd:option('-LearningRate',0.01,'learning rate')
  cmd:option('-InnerIteration',5,'set the number of inner iteration')
  cmd:option('-OuterIteration',2,'set the number of outer iteration')
  cmd:option('-DescentFunction',1,'0 is stochastic gradient descent, 1 is regular gradient descent')
  cmd:option('-BatchSize',500,'the number of gradients should be sum')
  cmd:text()

  params = cmd:parse(arg)

  learningRate = params.LearningRate
  innerIteration = params.InnerIteration
  outerIteration = params.OuterIteration
  batchsize = params.BatchSize
  -- print("Begin to train!")
  --load the data
  total_dataset, training_dataset, testing_dataset, classes, classes_names = dofile('read_dataset.lua')
  --print(training_dataset)
  -- print("Finish Loading!")
 
  if params.RunFunction == 1 then
 
    train_size = training_dataset:size()
    batch_number = math.modf(train_size/batchsize)+1
    -- print("batch_number", batch_number)
    -- print("About to shuffle the order !")
    ----shuffle the order of the whole dataset----
    local training_data={}
    local shuffledIndices = {}
    local f = io.open('order.random')
    for line in f:lines() do
      table.insert(shuffledIndices, tonumber(line))
    end
    for i=1,training_dataset:size() do
      local example = training_dataset[shuffledIndices[i]]
      training_data[i]=example
    end
    training_dataset=training_data

    --initalize network
    network = create_network()
    --begin to train
    for maxIterations=1,outerIteration do
      -- print("quux this is outer iteration number " .. maxIterations)
      train(network)
    end
    --test the final model
    test_predictor(network, testing_dataset, classes, classes_names)

  elseif params.RunFunction == 0 then
    
    local random_idx_list = {}
    local f = io.open('orderpy.random')
    for line in f:lines() do
      table.insert(random_idx_list, tonumber(line))
    end

    -- random_idx_list = {}
    -- for j = 1, tonumber(total_dataset:size()) do
    --   random_idx_list[j] = j
    -- end
    -- random_idx_list = shuffle2(random_idx_list)
    --print("shuffled data", random_idx_list)   
    
    --we're going to get the indices for all 5 cross folds
    five_fold_idxs = {}
    block_size = math.modf(total_dataset:size()/5)
    for i = 0, 4 do
      if i == 4 then
        five_fold_idxs[4] = {1 + (block_size * i), tonumber(total_dataset:size())}
      end
      five_fold_idxs[i] = {1 + (block_size * i), block_size * (i+1)}
    end 
    -- print("indxs: ", five_fold_idxs)
       
    training_data = {} 
    testing_data = {}

    precision_list = {}
    recall_list = {}
    accuracy_list = {}

    roc_curve_points = {}

    for m = 0,4 do
      -- print ("cross validation #", m)
      testing_counter = 1
      for midIdx = five_fold_idxs[m][1], five_fold_idxs[m][2] do
        testing_data[testing_counter] = total_dataset[midIdx]
        testing_counter = testing_counter + 1
      end
      
      training_counter = 1
      for training_idx = 0,4 do
        if tonumber(training_idx) ~= m then
          for midIdxTraining = five_fold_idxs[training_idx][1], five_fold_idxs[training_idx][2] do
            training_data[training_counter] = total_dataset[midIdxTraining]
            training_counter = training_counter + 1
          end
        end 
      end

      -- print('length of testing_data', table.getn(testing_data))
      train_size = table.getn(testing_data)
      batch_number = math.modf(train_size/batchsize)+1
      -- print("batch_number", batch_number)
      -- print("About to shuffle the order !")
      ----shuffle the order of the whole dataset----

      -- local training_data={}
      -- local testing_data = {}
      -- for i=1,tonumber(train_size) do
      --   local example = total_dataset[random_idx_list[i]]
      --   training_data[i]=example
      -- end
      -- training_dataset=training_data

      training_dataset = training_data

      --now we are going to get the testing dataset, which continues from the training dataset
      -- print("train size", train_size)
      -- print("starting number", tonumber(train_size) + 1)
      -- print("total size", tonumber(total_dataset:size()))

      -- local counter = 1
      -- for i=tonumber(train_size) + 1, tonumber(total_dataset:size()) do
      --   local example = total_dataset[random_idx_list[i]]
      --  -- print ("heres an exmaple! : ", example)
      --   testing_data[counter] = example
      --   counter = counter + 1
      -- end
      testing_dataset = testing_data
      
      -- print("testign data", testing_dataset)
      --initalize network
      network = create_network()
      --begin to train
      for maxIterations=1,outerIteration do
        -- print("quux this is outer iteration number " .. maxIterations)
        train(network)
      end
      --test the final model
      output_arr = test_predictor(network, testing_dataset, classes, classes_names)

      -- print('output array', output_arr)
      --we just take the first one, usually the best accuracy to average over 
      --the total list contains all 10 misclassification cost vectors that we used
      accuracy_list[m + 1] = output_arr[1][2]
      precision_list[m + 1] = output_arr[1][1]
      recall_list[m + 1] = output_arr[1][3]
      
      --now we get all points for ROC curve
      for i = 1, table.getn(output_arr) do
        roc_curve_points[i] = {output_arr[i][3],output_arr[i][4]} 
      end

    --cross validation end
    end

    avg_accuracy = mean(accuracy_list)
    avg_precision = mean(precision_list)
    avg_recall = mean(recall_list)

    -- print("avg accuracy", avg_accuracy)
    print (string.format("%f,%f,%f", avg_accuracy, avg_precision, avg_recall))
    print("\n")
    for i = 1, table.getn(roc_curve_points) do
      print(string.format("%f,%f", roc_curve_points[i][1], roc_curve_points[i][2]))
    end
    -- print("roc curve points", roc_curve_points)

  --elseif end
  end
--method end
end

function shuffle2(tbl)
  size = #tbl
  for i = size, 1, -1 do
    local rand = math.random(size)
    tbl[i], tbl[rand] = tbl[rand], tbl[i]
  end
  return tbl
end

function mean( t )
  local sum = 0
  local count= 0

  for k,v in pairs(t) do
    if type(v) == 'number' then
      sum = sum + v
      count = count + 1
    end
  end

  return (sum / count)
end

main()
