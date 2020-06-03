package eg.edu.alexu.csd.filestructure;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SignalFlowGraph {


    private static final int FORWARD_PATHS = 1;
    private static final int LOOPS = 2;

    private Map<String, Map<String, Float>> graph;

    private Map<List<String>, Float> forwardPaths = new HashMap<>();
    private Map<List<String>, Float> loops = new HashMap<>();
    private Map<List<String>, Map<Float, Integer>> nonTouchingLoops = new HashMap<>();

    public SignalFlowGraph() {
        graph = new HashMap<>();
    }

    public boolean addNode(String name) {
        if (graph.containsKey(name)) {
            System.out.println("plz Enter a valid name!, Duplication names aren't allowed");
            return false;
        }
        graph.put(name, new HashMap<>());
        return true;
    }

    public boolean addEdge(String from, String to, float value) {
        if (!graph.containsKey(from) || !graph.containsKey(to)) {
            System.out.println("check you entered valid nodes names!");
            return false;
        }
        graph.get(from).put(to, value);
        return true;
    }

    private void paths(String currentNode, String endNode, List<String> path) {
        path.add(currentNode);
        if (path.subList(0, path.size()-1).contains(currentNode)) {
            path = path.subList(path.indexOf(currentNode), path.size());
            if (!loops.containsKey(path)) {
                if (path.size() == 3) {
                    if (!loops.containsKey(Arrays.asList(path.get(1), path.get(0), path.get(1)))) {
                        System.out.println(path);
                        loops.put(path, null);
                    }
                } else {
                    System.out.println(path);
                    loops.put(path, null);
                }
            }
        } else if (currentNode.equals(endNode)){
            System.out.println(path);
            forwardPaths.put(path, null);}
        else {
            Iterator<String> iNeighbours = graph.get(currentNode).keySet().iterator();
            while (iNeighbours.hasNext())
                paths(iNeighbours.next(), endNode, new ArrayList<>(path));
        }
    }


    private void calculatePathsGain(int id) {
        Map<List<String>, Float> pathsRefrence = null;
        if (id == FORWARD_PATHS)
            pathsRefrence = forwardPaths;
        else if (id == LOOPS)
            pathsRefrence = loops;

        Iterator<Map.Entry<List<String>, Float>> iterator = pathsRefrence.entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry<List<String>, Float> path = iterator.next();
            float gain = 1;
            for (int i = 0; i < path.getKey().size() - 1; i++) {
                gain *= graph.get(path.getKey().get(i)).get(path.getKey().get(i + 1));
            }
            System.out.println(path + " " + gain);
            pathsRefrence.replace(path.getKey(), gain);
        }
    }

    private void getNonTouchingLoops() {
        int counter = 0;
        nonTouchingLoops.clear();
        for (Map.Entry<List<String>, Float> entry : loops.entrySet()) {
            Map<Float, Integer> helperMap = new HashMap<>();
            helperMap.put(entry.getValue(), counter);
            nonTouchingLoops.put(entry.getKey(), helperMap);
        }
        for (int i = 0; i < loops.size(); i++) {
            counter++;
            nonTouchingLoops.putAll(getithNonTouchingLoop(counter));
        }
    }

    private Map<List<String>, Map<Float, Integer>> getithNonTouchingLoop(int counter) {
        Map<List<String>, Map<Float, Integer>> newNonTouchingLoops = new HashMap<>();
        Iterator<Map.Entry<List<String>, Map<Float, Integer>>> iterator = nonTouchingLoops.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<List<String>, Map<Float, Integer>> item = iterator.next();
            List<String> path = item.getKey();

            if (item.getValue().values().iterator().next() == counter - 1) {
                Iterator<List<String>> currentpathIterator = loops.keySet().iterator();
                while (currentpathIterator.hasNext()) {
                    List<String> currentpath = currentpathIterator.next();
                    boolean found = true;
                    for (String node : path) {
                        if (currentpath.contains(node)) {
                            found = false;
                            break;
                        }
                    }
                    if (found) { // none touching loops found.
                        float gain = 1;
                        gain *= nonTouchingLoops.get(path).keySet().iterator().next();
                        gain *= loops.get(currentpath);
                        Map<Float, Integer> helperMap = new HashMap<>();
                        helperMap.put(gain, counter);
                        currentpath.addAll(path);
                        if (!newNonTouchingLoops.containsKey(Stream.concat(path.stream(),currentpath.stream()).collect(Collectors.toList())))
                            newNonTouchingLoops.put(currentpath, helperMap);
                    }
                }
            }
        }

        return newNonTouchingLoops;

    }

    private float calculateDelta() {
        float delta = 1;
        Iterator<Map<Float, Integer>> iterator = nonTouchingLoops.values().iterator();
        while (iterator.hasNext()) {
            Map<Float, Integer> entry = iterator.next();
            delta += Math.pow(-1, entry.values().iterator().next()) * entry.keySet().iterator().next();
        }
        return delta;
    }

    private float calculateDeltaI(List<String> path) {
        float deltaI = 1;
        Iterator<List<String>> iterator = loops.keySet().iterator();
        while (iterator.hasNext()) {
            List<String> loopPath = iterator.next();
            boolean flag = true;
            for (String node : loopPath) {
                if (path.contains(node)) {
                    flag = false;
                    break;
                }
            }
            if (flag)
                deltaI -= loops.get(path);
        }
        return deltaI;
    }

    public float calculateTransferFunction(String startNode, String endNode) {
        if (!graph.containsKey(startNode) || !graph.containsKey(endNode))
            System.out.println("check you entered valid nodes names!");
        else {
            paths(startNode, endNode,new ArrayList<String>());
            calculatePathsGain(FORWARD_PATHS);
            calculatePathsGain(LOOPS);
            getNonTouchingLoops();

            float delta = calculateDelta();
            float result = 0;
            Iterator<List<String>> iterator = forwardPaths.keySet().iterator();
            while (iterator.hasNext()) {
                List<String> path = iterator.next();
                result += forwardPaths.get(path) * calculateDeltaI(path);
            }
            result /= delta;

            return result;
        }
        return -1;
    }

}
