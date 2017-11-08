import unittest
import Trains

class GraphTest(unittest.TestCase):

	def test(self):
		graph = Graph()
		graph.add_edge('A','B',5)
		graph.add_edge('B', 'C', 4)
		graph.add_edge('C', 'D', 8)
		graph.add_edge('D', 'C', 8)
		graph.add_edge('D', 'E', 6)
		graph.add_edge('A', 'D', 5)
		graph.add_edge('C', 'E', 2)
		graph.add_edge('E', 'B', 3)
		graph.add_edge('A', 'E', 7)

		
