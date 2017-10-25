require "torch"
require "nn"
require "math"


-- global variables
learningRate = 0.01
innerIteration = 1
outerIteration = 1
fraction = 0.01
batchsize = 500

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

-- here we set up the architecture of the neural network
-- input raw data 561 * 1
--ANN with two hidden layer
function create_network()

  local ann = nn.Sequential(); 

  ann:add(nn.View(561 * 1))
  ann:add(nn.Linear(561, 300))
  ann:add(nn.ReLU())

  ann:add(nn.Linear(300, 100))
  ann:add(nn.ReLU())

  --output layer
  ann:add(nn.Linear(100, 12))
  ann:add(nn.ReLU())
  ann:add(nn.LogSoftMax())

  return ann
end

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
    print("quux this is inner iteration number " .. iteration)
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

  local mistakes = 0
  local tested_samples = 0

  for i = 1, table.getn(test_dataset) do
    local input = test_dataset[i][1]
    local class_id = test_dataset[i][2]
    local responses_per_class = predictor:forward(input)
    local probabilites_per_class = torch.exp(responses_per_class)
    local max_confidence_score = 0.0
    for j= i, 12 do
      print(probabilites_per_class[j])
      if tonumber(probabilites_per_class[j]) > max_confidence_score then
        max_confidence_score = tonumber(probabilites_per_class[j])
      end
    end
    print("the max confidence score is", max_confidence_score)
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

    if prediction[1] ~= class_id[1] then
      mistakes = mistakes + 1
      local label = classes_names[classes[class_id[1]]]
      local predicted_label = classes_names[classes[prediction[1]]]  
      --print(i , label , predicted_label )   
    end

    tested_samples = tested_samples + 1
  end

  local test_err = mistakes / tested_samples
  local accuracy = 1 - test_err

  print("accuracy = " .. accuracy)
end

function train(network)
  for index=0,batch_number do
    local dataIndex = index
    print("batch number:" .. dataIndex)
    print("---start training---!")
    local startPoint = 1+dataIndex*batchsize
    local endPoint = dataIndex*batchsize+batchsize
    --If endpoint is not exceed the size of the training set,
    --we update the model using current batch. 
    if endPoint<train_size then
      local current_batch = subset(training_dataset, startPoint, endPoint)
      ---start training---
      ----------------------------------------------
      update(network, current_batch)
      print("---start testing---!")
      test_predictor(network, testing_dataset, classes, classes_names)
      print("Finish Training!")
    --If endpoint is exceed the size of the training set but startPoint is not exceed,
    --we also update the model using current batch.
    elseif startPoint<train_size then
      local current_batch = subset(training_dataset, startPoint, train_size)
      ---start training---
      ----------------------------------------------
      update(network, current_batch)
      print("---start testing---!")
      test_predictor(network, testing_dataset, classes, classes_names)
      print("Finish Training!")
    --Done.
    else
      print('I am done.')
    end
  end
end

-- main routine
function main()
  print("Begin to train!")
  --load the data
  training_dataset, testing_dataset, classes, classes_names = dofile('read_dataset.lua')
  --print(training_dataset)
  print("Finish Loading!")
  
  train_size = training_dataset:size()
  batch_number = math.modf(train_size/batchsize)+1
  print("batch_number", batch_number)
  print("About to shuffle the order !")
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
    print("quux this is outer iteration number " .. maxIterations)
    train(network)
  end
  --test the final model
  test_predictor(network, testing_dataset, classes, classes_names)
end

main()
