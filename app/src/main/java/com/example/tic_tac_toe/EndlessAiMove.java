package com.example.tic_tac_toe;

public class EndlessAiMove {
    private int[][] board;//数组棋盘

    private static final int Empty = 0;
    private static final int PLAYER = 1;
    private static final int AI = 2;

    public int playerOrAi = 3;//1表示玩家取得三连，2表示AI,3表示都为取得

    //游戏开始时将正个棋盘设置为0
    public void GameStart() {
        board = new int[3][3];
    }

    //当玩家下棋时，设置对应的数字
    public void PlayerMove(int row, int col) {
        int[][] emptyIndexes = GetEmptyIndex();

        board[row][col] = PLAYER;
    }

    //保存空的数组位置
    private int[][] GetEmptyIndex() {
        int count = 0;
        // 计算空位的数量
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == Empty) {
                    count++;
                }
            }
        }

        // 创建一个数组来保存空位的索引,<空格位数组索引>
        int[][] emptyIndexes = new int[count][2];
        int index = 0;
        // 将空位的索引存储到数组中
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == Empty) {
                    emptyIndexes[index][0] = i;//<空格位数组索引>存储的行索引
                    emptyIndexes[index][1] = j;//<空格位数组索引>存储的列索引
                    index++;
                }
            }
        }
        return emptyIndexes;
    }

    //确定获胜的数组组合,board为棋盘位置,player为当前玩家1表示玩家，2表示ai
    private int[][] Winning(int[][] board, int player) {
        //定义达成三联的数组位置
        int[][] winPosition = new int[3][2];

        if(board[0][0] == player && board[0][1] == player && board[0][2] == player){
            return new int[][]{{0,0},{0,1},{0,2}};
        }
        else if (board[1][0] == player && board[1][1] == player && board[1][2] == player){
            return new int[][]{{1,0},{1,1},{1,2}};
        }
        else if (board[2][0] == player && board[2][1] == player && board[2][2] == player) {
            return new int[][]{{2,0},{2,1},{2,2}};
        }
        else if (board[0][0] == player && board[1][0] == player && board[2][0] == player) {
            return new int[][]{{0,0},{1,0},{2,0}};
        }
        else if (board[0][1] == player && board[1][1] == player && board[2][1] == player) {
            return new int[][]{{0, 1}, {1, 1}, {2, 1}};
        }else if (board[0][2] == player && board[1][2] == player && board[2][2] == player) {
            return new int[][]{{0, 2}, {1, 2}, {2, 2}};
        }
        else if (board[0][0] == player && board[1][1] == player && board[2][2] == player) {
            return new int[][]{{0, 0}, {1, 1}, {2, 2}};
        }
        else if (board[0][2] == player && board[1][1] == player && board[2][0] == player) {
            return new int[][]{{0, 2}, {1, 1}, {2, 0}};
        }
        return winPosition = null;
    }

    public int[][] getWinOrLose() {
        int[][] winPosition;

        // 玩家达成三连，返回达成三连的数组位置
        if ((winPosition = Winning(board, PLAYER)) != null) {
            playerOrAi = 1;//玩家取得分数
            return winPosition;
        }
        // AI达成三连，返回达成三连的数组位置
        else if ((winPosition = Winning(board, AI)) != null) {
            playerOrAi = 2;//Ai取得分数
            return winPosition;
        }else {//都为取得三联
            //玩家和ai都未获胜则返回null,游戏未结束或平局
            playerOrAi = 3;
            return null;
        }
    }

    // 清空达成三连的数组位置
    public void clearWinningPositions(int[][] winPosition) {
        if (winPosition != null) {
            for (int[] pos : winPosition) {
                board[pos[0]][pos[1]] = Empty;
            }
        }
    }

    //计算极大极小中的分数,与空位数组的长度
    private int MinMax(int player) {
        int[][] winPosition;

        //初始化bestScore如果是Ai初始化为最小的值；如果为玩家则初始化最大的值
        //目的：AI得分最大化，任何行动都可能提高它；玩家得分最小化，玩家任何行动都可能减少它
        int bestScore = (player==AI)?Integer.MIN_VALUE:Integer.MAX_VALUE;
        int[][] availSport = GetEmptyIndex();//得到空数组
        int score;

        //ai获胜返回10，玩家获胜返回-10，平局返回0
        if ((winPosition = Winning(board, AI)) != null) {
            return 10;
        } else if ((winPosition = Winning(board, PLAYER)) != null) {
            return -10;
        } else if (availSport.length == 0) {
            return 0;
        }
        //遍历数组时收集每一步的索引和分数存储其中
        for (int i = 0; i < availSport.length; i++) {
            int row = availSport[i][0];//空数组的 行记录索引
            int col = availSport[i][1];//空数组的 列记录索引

            board[row][col] = player;//将当前空位设置为玩家，或是AI；为了模拟玩家或AI在此位置下棋的情况

            //判断下完棋后是否获胜
            if (player == AI) {
                score = MinMax(PLAYER);//玩家取最小
                bestScore = Math.max(bestScore,score);
            } else {
                score = MinMax(AI);//敌人取最大
                bestScore = Math.min(bestScore,score);
            }
            board[row][col] = Empty;//因为是模拟最后要重置为空
        }
        return bestScore;
    }

    public int AIMove(){
        int bestScore = Integer.MIN_VALUE;//初始化为最小的值
        int moveRow = -1;//初始化为无效值-1
        int moveCol = -1;//方便更新后移动他们，如果未移动，则保持不变

        //得到空格数组
        int[][] availSpots = GetEmptyIndex();
        for (int i = 0;i < availSpots.length;i++){
            int row = availSpots[i][0];//获取空数组存储的行数
            int col = availSpots[i][1];//获取空数组存储的列数

            board[row][col] = AI;//设置为ai的最佳移动位置
            int moveScore = MinMax(PLAYER);//玩家取最小
            board[row][col] = Empty;

            if (moveScore > bestScore){
                bestScore = moveScore;
                moveRow = row;//如果moveScore > bestScore则更新最佳位置
                moveCol = col;
            }

            //board[moveRow][moveCol] = AI;
        }

        if (moveRow != -1) {
            board[moveRow][moveCol] = AI;//将结果置于for循环外部以免总是将结果置于最后
            return moveRow * 3 + moveCol;
        } else {
            return -1;
        }
    }
}
