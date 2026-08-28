package com.agrifleet.network_service.algorithm.kruskal;

public class DisjointSet {

    private final int[] parent;
    private final int[] rank;

    public DisjointSet(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("DSU size must be greater than zero.");
        }

        parent = new int[size + 1];
        rank = new int[size + 1];

        for (int i = 0; i <= size; i++) {
            parent[i] = i;
            rank[i] = 0;
        }
    }

    public int find(int node) {

        validateNode(node);

        if (parent[node] != node) {
            parent[node] = find(parent[node]);
        }

        return parent[node];
    }

    public boolean union(int nodeA, int nodeB) {

        validateNode(nodeA);
        validateNode(nodeB);

        int rootA = find(nodeA);
        int rootB = find(nodeB);

        if (rootA == rootB) {
            return false;
        }

        if (rank[rootA] < rank[rootB]) {
            parent[rootA] = rootB;
        } else if (rank[rootA] > rank[rootB]) {
            parent[rootB] = rootA;
        } else {
            parent[rootB] = rootA;
            rank[rootA]++;
        }

        return true;
    }

    private void validateNode(int node) {
        if (node < 0 || node >= parent.length) {
            throw new IllegalArgumentException(
                    "Node index out of range: " + node
            );
        }
    }
}
