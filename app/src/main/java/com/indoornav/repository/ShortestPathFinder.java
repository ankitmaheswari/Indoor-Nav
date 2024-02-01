package com.indoornav.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ShortestPathFinder {

    private final int[][] layoutPlan;

    public ShortestPathFinder(ArrayList<ArrayList<Integer>> layoutPlan) {
        int[][] plan = new int[layoutPlan.size()][layoutPlan.get(0).size()];
        for (int r = 0; r < layoutPlan.size(); r++) {
            for (int c = 0; c < layoutPlan.get(0).size(); c++) {
                plan[r][c] = layoutPlan.get(r).get(c);
            }
        }
        this.layoutPlan = plan;
    }


    public ArrayList<ArrayList<Integer>> findOptimalPath(int[] start, int[] end) {
         int[][] result = aStarSearch(layoutPlan, start, end, layoutPlan.length, layoutPlan[0].length);
         ArrayList<ArrayList<Integer>> list = new ArrayList<ArrayList<Integer>>();
         for (int r = 0; r < result.length; r++) {
             ArrayList l = new ArrayList();
             for (int c = 0; c < result[0].length; c++) {
                 l.add(result[r][c]);
             }
             list.add(l);
         }
         return list;
    }


    class Cell {
        int parent_i, parent_j;
        double f, g, h;

        Cell() {
            this.parent_i = 0;
            this.parent_j = 0;
            this.f = 0;
            this.g = 0;
            this.h = 0;
        }
    }

    private static boolean isValid(int row, int col, int pointX, int pointY) {
        return (pointX >= 0) && (pointX < row) && (pointY >= 0) && (pointY < col);
    }

    private static boolean isUnBlocked(int[][] grid, int row, int col) {
        return grid[row][col] == 0;
    }

    private static boolean isDestination(int row, int col, int[] dest) {
        return row == dest[0] && col == dest[1];
    }

    private static double calculateHValue(int row, int col, int[] dest) {
        return Math.sqrt((row - dest[0]) * (row - dest[0]) + (col - dest[1]) * (col - dest[1]));
    }

    private static int[][] tracePath(Cell[][] cellDetails, int[] dest) {
        System.out.println("The Path is ");
        int row = dest[0];
        int col = dest[1];

        Map<int[], Boolean> path = new LinkedHashMap<>();

        while (!(cellDetails[row][col].parent_i == row && cellDetails[row][col].parent_j == col)) {
            path.put(new int[]{row, col}, true);
            int temp_row = cellDetails[row][col].parent_i;
            int temp_col = cellDetails[row][col].parent_j;
            row = temp_row;
            col = temp_col;
        }

        path.put(new int[]{row, col}, true);
        List<int[]> pathList = new ArrayList<>(path.keySet());
        Collections.reverse(pathList);
        int result[][] = new int[pathList.size()][2];

        final int[] i = {0};
        pathList.forEach(p -> {
            result[i[0]][0] = p[0];
            result[i[0]][1] = p[1];
            i[0]++;
            if (p[0] == 2 || p[0] == 1) {
                System.out.print("-> (" + p[0] + ", " + (p[1]) + ")");
            } else {
                System.out.print("-> (" + p[0] + ", " + p[1] + ")");
            }
        });
        System.out.println();
        return result;
    }

    private  int[][] aStarSearch(int[][] grid, int[] src, int[] dest, int row, int col) {
        if (!isValid(row, col, src[0], src[1]) || !isValid(row, col, dest[0], dest[1])) {
            System.out.println("Source or destination is invalid");
            return new int[0][0];
        }

        if (!isUnBlocked(grid, src[0], src[1]) || !isUnBlocked(grid, dest[0], dest[1])) {
            System.out.println("Source or the destination is blocked");
            return new int[0][0];
        }

        if (isDestination(src[0], src[1], dest)) {
            System.out.println("We are already at the destination");
            return new int[0][0];
        }

        boolean[][] closedList = new boolean[row][col];
        Cell[][] cellDetails = new Cell[row][col];

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                cellDetails[i][j] = new Cell();
                cellDetails[i][j].f = Double.POSITIVE_INFINITY;
                cellDetails[i][j].g = Double.POSITIVE_INFINITY;
                cellDetails[i][j].h = Double.POSITIVE_INFINITY;
                cellDetails[i][j].parent_i = -1;
                cellDetails[i][j].parent_j = -1;
            }
        }

        int i = src[0], j = src[1];
        cellDetails[i][j].f = 0;
        cellDetails[i][j].g = 0;
        cellDetails[i][j].h = 0;
        cellDetails[i][j].parent_i = i;
        cellDetails[i][j].parent_j = j;

        Map<Double, int[]> openList = new HashMap<>();
        openList.put(0.0, new int[]{i, j});

        boolean foundDest = false;

        while (!openList.isEmpty()) {
            Map.Entry<Double, int[]> p = openList.entrySet().iterator().next();
            openList.remove(p.getKey());

            i = p.getValue()[0];
            j = p.getValue()[1];
            closedList[i][j] = true;

            double gNew, hNew, fNew;

            // 1st Successor (North)
            if (isValid(row, col, i - 1, j)) {
                if (isDestination(i - 1, j, dest)) {
                    cellDetails[i - 1][j].parent_i = i;
                    cellDetails[i - 1][j].parent_j = j;
                    System.out.println("The destination cell is found");
                    return tracePath(cellDetails, dest);
                    /*foundDest = true;
                    return;*/
                } else if (!closedList[i - 1][j] && isUnBlocked(grid, i - 1, j)) {
                    gNew = cellDetails[i][j].g + 1;
                    hNew = calculateHValue(i - 1, j, dest);
                    fNew = gNew + hNew;

                    if (cellDetails[i - 1][j].f == Double.POSITIVE_INFINITY

                            || cellDetails[i - 1][j].f > fNew) {
                        openList.put(fNew, new int[]{i - 1, j});

                        cellDetails[i - 1][j].f = fNew;
                        cellDetails[i - 1][j].g = gNew;
                        cellDetails[i - 1][j].h = hNew;
                        cellDetails[i - 1][j].parent_i = i;
                        cellDetails[i - 1][j].parent_j = j;
                    }
                }
            }

            // 2nd Successor (South)
            if (isValid(row, col, i + 1, j)) {
                if (isDestination(i + 1, j, dest)) {
                    cellDetails[i + 1][j].parent_i = i;
                    cellDetails[i + 1][j].parent_j = j;
                    System.out.println("The destination cell is found");
                    return tracePath(cellDetails, dest);
                    /*foundDest = true;
                    return;*/
                } else if (!closedList[i + 1][j] && isUnBlocked(grid, i + 1, j)) {
                    gNew = cellDetails[i][j].g + 1;
                    hNew = calculateHValue(i + 1, j, dest);
                    fNew = gNew + hNew;

                    if (cellDetails[i + 1][j].f == Double.POSITIVE_INFINITY || cellDetails[i + 1][j].f > fNew) {
                        openList.put(fNew, new int[]{i + 1, j});

                        cellDetails[i + 1][j].f = fNew;
                        cellDetails[i + 1][j].g = gNew;
                        cellDetails[i + 1][j].h = hNew;
                        cellDetails[i + 1][j].parent_i = i;
                        cellDetails[i + 1][j].parent_j = j;
                    }
                }
            }

            // 3rd Successor (East)
            if (isValid(row, col, i, j + 1)) {
                if (isDestination(i, j + 1, dest)) {
                    cellDetails[i][j + 1].parent_i = i;
                    cellDetails[i][j + 1].parent_j = j;
                    System.out.println("The destination cell is found");
                    return tracePath(cellDetails, dest);
                    /*foundDest = true;
                    return;*/
                } else if (!closedList[i][j + 1] && isUnBlocked(grid, i, j + 1)) {
                    gNew = cellDetails[i][j].g + 1;
                    hNew = calculateHValue(i, j + 1, dest);
                    fNew = gNew + hNew;

                    if (cellDetails[i][j + 1].f == Double.POSITIVE_INFINITY || cellDetails[i][j + 1].f > fNew) {
                        openList.put(fNew, new int[]{i, j + 1});

                        cellDetails[i][j + 1].f = fNew;
                        cellDetails[i][j + 1].g = gNew;
                        cellDetails[i][j + 1].h = hNew;
                        cellDetails[i][j + 1].parent_i = i;
                        cellDetails[i][j + 1].parent_j = j;
                    }
                }
            }

            // 4th Successor (West)
            if (isValid(row, col, i, j - 1)) {
                if (isDestination(i, j - 1, dest)) {
                    cellDetails[i][j - 1].parent_i = i;
                    cellDetails[i][j - 1].parent_j = j;
                    System.out.println("The destination cell is found");
                    return tracePath(cellDetails, dest);
                    /*foundDest = true;
                    return;*/
                } else if (!closedList[i][j - 1] && isUnBlocked(grid, i, j - 1)) {
                    gNew = cellDetails[i][j].g + 1;
                    hNew = calculateHValue(i, j - 1, dest);
                    fNew = gNew + hNew;

                    if (cellDetails[i][j - 1].f == Double.POSITIVE_INFINITY || cellDetails[i][j - 1].f > fNew) {
                        openList.put(fNew, new int[]{i, j - 1});

                        cellDetails[i][j - 1].f = fNew;
                        cellDetails[i][j - 1].g = gNew;
                        cellDetails[i][j - 1].h = hNew;
                        cellDetails[i][j - 1].parent_i = i;
                        cellDetails[i][j - 1].parent_j = j;
                    }
                }
            }

            /*// 5th Successor (North-East)
            if (isValid(row, col, i - 1, j + 1)) {
                if (isDestination(i - 1, j + 1, dest)) {
                    cellDetails[i - 1][j + 1].parent_i = i;
                    cellDetails[i - 1][j + 1].parent_j = j;
                    System.out.println("The destination cell is found");
                    return tracePath(cellDetails, dest);
                    *//*foundDest = true;
                    return;*//*
                } else if (!closedList[i - 1][j + 1] && isUnBlocked(grid, i - 1, j + 1)) {
                    gNew = cellDetails[i][j].g + 1.414;
                    hNew = calculateHValue(i - 1, j + 1, dest);
                    fNew = gNew + hNew;

                    if (cellDetails[i - 1][j + 1].f == Double.POSITIVE_INFINITY || cellDetails[i - 1][j + 1].f > fNew) {
                        openList.put(fNew, new int[]{i - 1, j + 1});

                        cellDetails[i - 1][j + 1].f = fNew;
                        cellDetails[i - 1][j + 1].g = gNew;
                        cellDetails[i - 1][j + 1].h = hNew;
                        cellDetails[i - 1][j + 1].parent_i = i;
                        cellDetails[i - 1][j + 1].parent_j = j;
                    }
                }
            }

            // 6th Successor (North-West)
            if (isValid(row, col, i - 1, j - 1)) {
                if (isDestination(i - 1, j - 1, dest)) {
                    cellDetails[i - 1][j - 1].parent_i = i;
                    cellDetails[i - 1][j - 1].parent_j = j;
                    System.out.println("The destination cell is found");
                    return tracePath(cellDetails, dest);
                    *//*foundDest = true;
                    return;*//*
                } else if (!closedList[i - 1][j - 1] && isUnBlocked(grid, i - 1, j - 1)) {
                    gNew = cellDetails[i][j].g + 1.414;
                    hNew = calculateHValue(i - 1, j - 1, dest);
                    fNew = gNew + hNew;

                    if                    (cellDetails[i - 1][j - 1].f == Double.POSITIVE_INFINITY || cellDetails[i - 1][j - 1].f > fNew) {
                        openList.put(fNew, new int[]{i - 1, j - 1});

                        cellDetails[i - 1][j - 1].f = fNew;
                        cellDetails[i - 1][j - 1].g = gNew;
                        cellDetails[i - 1][j - 1].h = hNew;
                        cellDetails[i - 1][j - 1].parent_i = i;
                        cellDetails[i - 1][j - 1].parent_j = j;
                    }
                }
            }

            // 7th Successor (South-East)
            if (isValid(row, col, i + 1, j + 1)) {
                if (isDestination(i + 1, j + 1, dest)) {
                    cellDetails[i + 1][j + 1].parent_i = i;
                    cellDetails[i + 1][j + 1].parent_j = j;
                    System.out.println("The destination cell is found");
                    return tracePath(cellDetails, dest);
                    *//*foundDest = true;
                    return;*//*
                } else if (!closedList[i + 1][j + 1] && isUnBlocked(grid, i + 1, j + 1)) {
                    gNew = cellDetails[i][j].g + 1.414;
                    hNew = calculateHValue(i + 1, j + 1, dest);
                    fNew = gNew + hNew;

                    if (cellDetails[i + 1][j + 1].f == Double.POSITIVE_INFINITY || cellDetails[i + 1][j + 1].f > fNew) {
                        openList.put(fNew, new int[]{i + 1, j + 1});

                        cellDetails[i + 1][j + 1].f = fNew;
                        cellDetails[i + 1][j + 1].g = gNew;
                        cellDetails[i + 1][j + 1].h = hNew;
                        cellDetails[i + 1][j + 1].parent_i = i;
                        cellDetails[i + 1][j + 1].parent_j = j;
                    }
                }
            }

            // 8th Successor (South-West)
            if (isValid(row, col, i + 1, j - 1)) {
                if (isDestination(i + 1, j - 1, dest)) {
                    cellDetails[i + 1][j - 1].parent_i = i;
                    cellDetails[i + 1][j - 1].parent_j = j;
                    System.out.println("The destination cell is found");
                    return tracePath(cellDetails, dest);
                    *//*foundDest = true;
                    return;*//*
                } else if (!closedList[i + 1][j - 1] && isUnBlocked(grid, i + 1, j - 1)) {
                    gNew = cellDetails[i][j].g + 1.414;
                    hNew = calculateHValue(i + 1, j - 1, dest);
                    fNew = gNew + hNew;

                    if (cellDetails[i + 1][j - 1].f == Double.POSITIVE_INFINITY || cellDetails[i + 1][j - 1].f > fNew) {
                        openList.put(fNew, new int[]{i + 1, j - 1});

                        cellDetails[i + 1][j - 1].f = fNew;
                        cellDetails[i + 1][j - 1].g = gNew;
                        cellDetails[i + 1][j - 1].h = hNew;
                        cellDetails[i + 1][j - 1].parent_i = i;
                        cellDetails[i + 1][j - 1].parent_j = j;
                    }
                }
            }*/
        }

        if (!foundDest)
            System.out.println("Failed to find the destination cell");
        return new int[0][0];
    }
}