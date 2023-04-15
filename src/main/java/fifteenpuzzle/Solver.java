package fifteenpuzzle;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

public class Solver {

  private static final int[][] DIRS = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
  private int[] board;
  private int n;

  public Solver(int[] board, int n) {
    this.board = board;
    this.n = n;
  }
  public boolean solve(Map<Integer, String> resultMap) {
    Map<String, String> prev = new HashMap<>();
    Queue<String> queue = new LinkedList<>();
    String initial = getBoardString(board);
    prev.put(initial, "");
    queue.offer(initial);

    while (!queue.isEmpty()) {

      String curr = queue.poll();
      int[] currBoard = getBoardArray(curr);
      if (isSolved(currBoard)) {
        // Add moves to resultMap
        String moves = prev.get(curr);
        String[] moveArr = moves.split(",");
        for (String move : moveArr) {
          String[] actualMoves = move.trim().split("\\s+");
          for (int i = 0; i < actualMoves.length; i++) {
            resultMap.put(i , actualMoves[i].replace("", " ").trim());
          }
        }
        return true;
      }

      int zeroIndex = getZeroIndex(currBoard);
      int x = zeroIndex / n, y = zeroIndex % n;
      for (int[] dir : DIRS) {
        int nx = x + dir[0], ny = y + dir[1];
        if (nx < 0 || ny < 0 || nx >= n || ny >= n) {
          continue;
        }
        int newIndex = nx * n + ny;
        int[] newBoard = Arrays.copyOf(currBoard, n * n);
        newBoard[zeroIndex] = newBoard[newIndex];
        newBoard[newIndex] = 0;
        String newBoardStr = getBoardString(newBoard);
        if (!prev.containsKey(newBoardStr)) {
          String move = getMove(newIndex, zeroIndex);
          int value = newBoard[zeroIndex];
          prev.put(newBoardStr, prev.get(curr) + " " +value + move);
          queue.offer(newBoardStr);
        }
      }
    }
    return false;
  }

  private String getMove(int newIndex, int zeroIndex) {
    if (newIndex - zeroIndex == n) {
      return "U";
    } else if (newIndex - zeroIndex == -n) {
      return "D";
    } else if (newIndex - zeroIndex == 1) {
      return "L";
    } else {
      return "R";
    }
  }

  private int[] getBoardArray(String boardStr) {
    String[] boardArr = boardStr.split(",");
    int[] res = new int[boardArr.length];
    for (int i = 0; i < boardArr.length; i++) {
      res[i] = Integer.parseInt(boardArr[i]);
    }
    return res;
  }

  private String getBoardString(int[] boardArr) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < boardArr.length; i++) {
      sb.append(boardArr[i]);
      if (i != boardArr.length - 1) {
        sb.append(",");
      }
    }
    return sb.toString();
  }

  private boolean isSolved(int[] board) {
    for (int i = 0; i < board.length - 1; i++) {
      if (board[i] != i + 1) {
        return false;
      }
    }
    return true;
  }

  private int getZeroIndex(int[] board) {
    for (int i = 0; i < board.length; i++) {
      if (board[i] == 0) {
        return i;
      }
    }
    throw new IllegalArgumentException("Invalid board: missing zero");
  }

  public static void main(String[] args) {

    if (args.length != 2
    ) {
      System.out.println("Usage: java fifteenpuzzzle.Solver <input_file> <output_file>");
      return;
    }

    try {
      // Read input file
      BufferedReader br = new BufferedReader(new FileReader(args[0]));
      int n = Integer.parseInt(br.readLine().trim());
      int[] board = new int[n * n];
      for (int i = 0; i < n; i++) {
        String std = br.readLine();
        String dr = std.replace("   ", "  0");
        String[] row = dr.trim().split("\\s+");

        if (row.length < n) {
          String[] newArray = Arrays.copyOf(row, row.length + 1);
          newArray[newArray.length - 1] = String.valueOf(0);
          row = newArray;
        }
        for (int j = 0; j < n; j++) {
          board[i * n + j] = Integer.parseInt(row[j]);
        }
      }
      br.close();

      Solver solver = new Solver(board, n);
      Map<Integer, String> solution = new TreeMap<>();
      if (solver.solve(solution)) {
        // Write output file
        BufferedWriter bw = new BufferedWriter(new FileWriter(args[1]));
        for (Entry<Integer, String> entry : solution.entrySet()) {
          bw.write(entry.getKey() + " " + entry.getValue());
          bw.newLine();
        }
        bw.close();
      } else  {
        System.out.println("No solution found.");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}

