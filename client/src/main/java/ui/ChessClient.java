package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import client.NotifHandler;
import client.ServerFacade;
import client.WebSocketFacade;
import model.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.*;

import static ui.ChessBoard.*;

public class ChessClient implements NotifHandler {
    private String authToken = null;
    private final ServerFacade server;
    private WebSocketFacade ws;
    //private State state = state.SIGNEDOUT;
    private final String serverURL = "http://localhost:8080";
    private boolean loggedIn = false; // want to check if logged in first
    public final Map<Integer, Integer> gameListMap = new HashMap<>();
    private String playerColor;
    private ChessGame currGame;
    private int currGameID;

    public ChessClient() {
        this.server = new ServerFacade(serverURL);
    }

    public void run() {
        System.out.println("♕ Welcome to 240 Chess. Type Help to get Started. ♕");
        //System.out.print(help());
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while(!Objects.equals(result, "quit")){
            //check status of Login
            if(!loggedIn){
                System.out.print("\n[LOGGED_OUT] >>> ");
            }else{
                System.out.print("\n[LOGGED_IN] >>> ");
            }

            String line = scanner.nextLine();
            result = eval(line);

            System.out.print(result);
        }
        System.out.println(" Exiting...");
    }

    public String eval(String input){
        if(!loggedIn){
            return evalPre(input);
        }else{
            return evalPost(input);
        }
    }

    public String helpPre() {
        return """
                register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                login <USERNAME> <PASSWORD> - to play chess
                quit - playing chess
                help - with possible commands
                """;
    }

    //Prelogin Ui
    public String evalPre(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "help" -> helpPre();
                case "register" -> reg(params);
                case "login" -> logIn(params);
                case "quit" -> "quit";
                default -> helpPre();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String logIn(String... params){
        if (params.length != 2) {
            return "Must provide <USERNAME> and <PASSWORD>";
        }
        String username = params[0];
        String password = params[1];

        try{
            AuthData authData = server.login(username, password);

            this.loggedIn = true;
            this.authToken = authData.authToken();
            return  String.format("Successfull log in %s", username);

        }catch (Exception ex){
            return ex.getMessage();
        }
    }

    public String reg(String... params) {
        if (params.length != 3) {
            return "Must provide <USERNAME>, <PASSWORD>, and <EMAIL>";
        }
        String username = params[0];
        String password = params[1];
        String email = params[2];

        try{
            AuthData authData = server.register(username, password, email);

            this.loggedIn = true;
            this.authToken = authData.authToken();
            return  String.format("Successfull log in %s", username);

        }catch (Exception ex){
            return ex.getMessage();
        }
    }

    public String helpPost() {
        return """
                create <NAME> - a game
                list - games
                join <ID> [WHITE|BLACK] - a game
                observe <ID> - a game
                move <START> <END> - move piece
                highlight <POSITION> - possible moves
                logout - when you are done
                quit - playing chess
                help - with possible commands
                """;
    }

    //Postlogin UI
    public String evalPost(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "help" -> helpPost();
                case "create" -> createGame(params);
                case "list" -> list();
                case "move" -> makeMove(params);
                case "highlight" -> highlightMoves(params);
                case "join" -> joinGames(params);
                case "observe" -> observe(params);
                case "logout" -> logOut();
                case "quit" -> "quit";
                case "leave" -> leave();
                case "resign" -> resign();
                default -> helpPost();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    private String resign() {
        try{
            if(currGameID == 0){
                return "You are not in a game";
            }

            System.out.print("Are you sure you want to resign? <YES/NO>");
            Scanner scanner = new Scanner(System.in);
            String answer = scanner.nextLine().toLowerCase();

            if(!answer.equalsIgnoreCase("yes")){
                return "Canceling resignation";
            }

            ws.resignChess(authToken, currGameID);

            return "Resigned the game";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private String leave() {
        try{
            if(currGameID == 0){
                return "You are not in a game";
            }

            ws.leaveChess(authToken, currGameID);

            this.currGame = null;
            this.currGameID = 0;
            this.playerColor = null;

            return "Left the game";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private String highlightMoves(String[] params) {
        if(params.length != 1){
            return "Expected highlight <POSITION>";
        }

        try{
            ChessPosition position = getPos(params[0]);
            Collection<ChessMove> moves = currGame.validMoves(position);
            boolean white = (playerColor == null || playerColor.equalsIgnoreCase("white"));
            System.out.println(drawBoardHighlights(currGame.getBoard(), moves, white, position));
            return "";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String makeMove(String[] params) {
        if(params.length < 2){
            return "Must provide move <START> <END>";
        }

        //determine if observer
        try{
            ChessPosition startPos = getPos(params[0]);
            ChessPosition endPos = getPos(params[1]);

            ChessPiece.PieceType promotion = null;
            //figure out promotion
            if(params.length > 2){
                String prom = params[2].toUpperCase();
                if(prom.equals("Q")){
                    promotion = ChessPiece.PieceType.QUEEN;
                } else if (prom.equals("R")) {
                    promotion = ChessPiece.PieceType.ROOK;
                } else if (prom.equals("B")) {
                    promotion = ChessPiece.PieceType.BISHOP;
                } else if (prom.equals("N")) {
                    promotion = ChessPiece.PieceType.KNIGHT;
                }
            }

            ChessMove move = new ChessMove(startPos, endPos, promotion);
            if(this.currGameID == 0){
                return "You are not observing any game";
            }
            ws.makeMove(authToken, this.currGameID, move);
            return "Moving: " + params[0] + " to " + params[1];

        }catch (Exception ex){
            return ex.getMessage();
        }
    }

    private ChessPosition getPos(String param) {
        char col = param.charAt(0);
        char row = param.charAt(1);

        int column = col - 'a' + 1;
        int rows = row - '1' + 1;
        return new ChessPosition(rows, column);
    }

    public String logOut() {
        try{
            server.logout(this.authToken);

            this.loggedIn = false;
            this.authToken = null;
            return "You are logged out";

        }catch (Exception ex){
            return ex.getMessage();
        }
    }

    public String createGame(String... params) {
        if(params.length != 1){
            return "Must provide <GAME_NAME>";
        }
        String gameName = params[0];

        try{
            var createGameResponse = server.createGames(gameName, this.authToken);
            return String.format("Game %s created ", gameName);

        }catch (Exception ex){
            return ex.getMessage();
        }
    }

    public String list(){
        try{
            var games = server.listGames(this.authToken);
            StringBuilder out = new StringBuilder();
            gameListMap.clear();
            int count = 1;
            for(var game : games){
                gameListMap.put(count, game.gameID());
                out.append(String.format("%d. %s", count, game.gameName()));
                out.append(String.format(" White: %s\n", game.whiteUsername() != null
                ? game.whiteUsername() : "<empty> "));
                out.append(String.format(" Black: %s\n", game.blackUsername() != null
                ? game.blackUsername() : "<empty> "));
                count++;
            }
            return out.toString();

        }catch (Exception ex){
            return ex.getMessage();
        }
    }

    public String joinGames(String... params) {
        if(params.length != 2){
            return "Must provide <GAME_ID> and <WHITE|BLACK>";
        }

        try{
            String gameID = params[0];
            int listNum = Integer.parseInt(gameID);
            if(!gameListMap.containsKey(listNum)){
                return "Game num " + listNum + "doesn't exist. Check 'list'.";
            }
            String playerColor = params[1].toLowerCase();
            int gameIDactual = gameListMap.get(listNum);
            this.currGameID = gameIDactual;
            server.joinGame(gameIDactual, playerColor, this.authToken);
            this.playerColor = playerColor;
            if(ws == null){
                ws = new WebSocketFacade(serverURL, this);
            }
            ws.connectChess(authToken, gameIDactual);
            return String.format("Joining %s as %s.",gameID, playerColor);
        }catch (Exception ex){
            return ex.getMessage();
        }
    }

    public String observe(String... params){
        if(params.length != 1){
            return "Must provide <ID>";
        }
        try{
            String gameID = params[0];
            int listNum = Integer.parseInt(gameID);
            if(!gameListMap.containsKey(listNum)){
                return "Game num " + listNum + "doesn't exist. Check 'list'.";
            }
            int gameIDactual = gameListMap.get(listNum);
            if(authToken == null){
                return "Log in first";
            }
            //server.joinGame(gameIDactual, null, this.authToken);
            this.playerColor = "white";
            this.currGameID = gameIDactual;
            if(ws == null){
                ws = new WebSocketFacade(serverURL, this);
            }
            ws.connectChess(authToken, gameIDactual);
            return String.format("Observing game %s", gameID);
//            return msg + drawWhite();
        }catch (Exception ex){
            return ex.getMessage();
        }
    }

    @Override
    public void notify(ServerMessage notifMess) {
        switch (notifMess.getServerMessageType()){
            case LOAD_GAME -> {
                LoadGameMessage loadGameMessage = (LoadGameMessage) notifMess;
                ChessGame game = loadGameMessage.getGame();
                this.currGame = loadGameMessage.getGame();
                if(this.playerColor != null && this.playerColor.equalsIgnoreCase("black")){
                    System.out.println(drawBlack(game.getBoard()));
                    printPrompt();
                }else{
                    System.out.println(drawWhite(game.getBoard()));
                    printPrompt();
                }
            }
            case ERROR -> {
                ErrorMessage errorMessage = (ErrorMessage) notifMess;
                System.out.println(errorMessage.getErrorMess());
                printPrompt();
            }
            case NOTIFICATION -> {
                NotificationMessage notificationMessage = (NotificationMessage) notifMess;
                System.out.println(notificationMessage.getNotifMess());
                printPrompt();
            }
        }
    }

    public void setAuthToken(String authToken){
        this.authToken = authToken;
        this.loggedIn = true;
    }

    public void printPrompt(){
        System.out.print("\n[LOGGED_IN] >>> ");
    }

    //GamePlay UI

}
