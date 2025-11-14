package ui;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class ChessClient {
    private String authToken = null;
    //private final ServerFacade server;
    //private final WebSocketFacade ws;
    //private State state = state.SIGNEDOUT;
    private boolean loggedIn = false; // want to check if logged in first

//    public PetClient(String serverUrl) throws ResponseException {
//        server = new ServerFacade(serverUrl);
//        ws = new WebSocketFacade(serverUrl, this);
//    }

    public void run() {
        System.out.println(" Welcome to 240 Chess. Type Help to get Started.");
        //System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while(true){
            //check status of Login
            if(!loggedIn){
                System.out.print("[LOGGED_OUT] >>> ");
            }else{
                System.out.print("[LOGGED_IN] >>> ");
            }

            String line = scanner.nextLine();
            eval(line);
            System.out.print(result);
//            printPrompt();

//            try {
//                result = eval(line);
//                System.out.print(BLUE + result);
//            } catch (Throwable e) {
//                var msg = e.toString();
//                System.out.print(msg);
//            }
        }
    }

//    public void notify(Notification notification) {
//        System.out.println(RED + notification.message());
//        printPrompt();
//    }
//
//    private void printPrompt() {
//        System.out.print("\n" + RESET + ">>> " + GREEN);
//    }
//
//    public String listPets() throws ResponseException {
//        assertSignedIn();
//        PetList pets = server.listPets();
//        var result = new StringBuilder();
//        var gson = new Gson();
//        for (Pet pet : pets) {
//            result.append(gson.toJson(pet)).append('\n');
//        }
//        return result.toString();
//    }
//
//    public String adoptPet(String... params) throws ResponseException {
//        assertSignedIn();
//        if (params.length == 1) {
//            try {
//                int id = Integer.parseInt(params[0]);
//                Pet pet = getPet(id);
//                if (pet != null) {
//                    server.deletePet(id);
//                    return String.format("%s says %s", pet.name(), pet.sound());
//                }
//            } catch (NumberFormatException ignored) {
//            }
//        }
//        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <pet id>");
//    }
//
//    public String adoptAllPets() throws ResponseException {
//        assertSignedIn();
//        var buffer = new StringBuilder();
//        for (Pet pet : server.listPets()) {
//            buffer.append(String.format("%s says %s%n", pet.name(), pet.sound()));
//        }
//
//        server.deleteAllPets();
//        return buffer.toString();
//    }
//
//    public String signOut() throws ResponseException {
//        assertSignedIn();
//        ws.leavePetShop(visitorName);
//        state = State.SIGNEDOUT;
//        return String.format("%s left the shop", visitorName);
//    }
//
//    private Pet getPet(int id) throws ResponseException {
//        for (Pet pet : server.listPets()) {
//            if (pet.id() == id) {
//                return pet;
//            }
//        }
//        return null;
//    }
//
//    private void assertSignedIn() throws ResponseException {
//        if (state == State.SIGNEDOUT) {
//            throw new ResponseException(ResponseException.Code.ClientError, "You must sign in");
//        }
//    }

    public void eval(String input){
        if(!loggedIn){
            evalPre(input);
        }else{
            evalPost(input);
        }
    }

    //Prelogin Ui
    public String evalPre(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "help" -> help();
                case "register" -> reg(params);
                case "login" -> logIn(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String logIn(String... params) throws ResponseException {
//        if (params.length >= 1) {
//            state = State.SIGNEDIN;
//            visitorName = String.join("-", params);
//            ws.enterPetShop(visitorName);
            return String.format("You signed in as %s.", visitorName);
//        }
//        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <yourname>");
    }

    public String reg(String... params) throws ResponseException {
//        assertSignedIn();
//        if (params.length >= 2) {
//            String name = params[0];
//            PetType type = PetType.valueOf(params[1].toUpperCase());
//            var pet = new Pet(0, name, type);
//            pet = server.addPet(pet);
            return String.format("You rescued %s. Assigned ID: %d", pet.name(), pet.id());
//        }
//        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <name> <CAT|DOG|FROG>");
    }

    public String help() {
//        if (state == State.SIGNEDOUT) {
//            return """
//                    - signIn <yourname>
//                    - quit
//                    """;
//        }
        return """
                - help
                - register <USERNAME> <PASSWORD> <EMAIL>
                - login <USERNAME> <PASSWORD>
                - quit
                """;
    }

    //Postlogin UI
    public String evalPost(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "help" -> help();
                case "create" -> reg(params);
                case "list" -> logIn(params);
                case "join" -> logIn(params);
                case "observe" -> logIn(params);
                case "logout" -> logIn(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }
    //GamePlay UI
}
