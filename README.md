# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922



link to diagram
https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACU12qojulVk8iUKnU9XsKDAAFUBhi3h8UKTqYplGpVJSjDpagAxJCcGCsyg8mA6SwwDmzMQ6FHAADWkoGME2SDA8QVA05MGACFVHHlKAAHmiNDzafy7gjySp6lKoDyySIVI7KjdnjAFKaUMBze11egAKKWlTYAgFT23Ur3YrmeqBJzBYbjObqYCMhbLCNQbx1A1TJXGoMh+XyNXoKFmTiYO189Q+qpelD1NA+BAIBMU+4tumqWogVXot3sgY87nae1t+7GWoKDgcTXS7QD71D+et0fj4PohQ+PUY4Cn+Kz5t7keC5er9cnvUexE7+4wp6l7FovFqXtYJ+cLtn6pavIaSpLPU+wgheertBAdZoFByyXAmlDtimGD1OEThOFmEwQZ8MDQcCyxwfECFISh+xXOgHCmJ43h+P40DsIyMQinAEbSHACgwAAMhAWSFFhzBOtQ-rNG0XS9AY6j5GgWaKnMay-P8HBXKBgpAf6VAmkgWjokJInnpeVHoKSulQBUb5dsal7mIQcgoNO5aqTA6k7KSdmCsO-IMkybo8m57xKnONL7vewowGKEpvLK8oqWI-mLhJVBIhu7pbr56WZTUSAAGaWE0+h-DsGJeQCpIqsGGoAJJoCA0AouAnllRp26GEmvrVP6JnilGMZxoU2k9fAyCpjA6YAIwETmqh5vM0FFiW9Q+NMl7QEgABeKC7HRTa5R+jxwvUA1oASRLMBiqg+PuVmnRh42gS8FEWWg0HJUsWl9fCyaTdhMC4fhoyEe58wkahqxjO9iH1lDtGNgxTG+AEXgoOgMRxIkGNYyZvhYGJgqvY00gRgJEbtBG3Q9PJqiKcMcNIehNmVNZ9TxE9BOnjAZnwfDaCPbClC2c6XYIMJhP85Rgs+eLFSpaOjJgM+8Qyx9EW8iOS4xXF64UTKcoOQLLO1eqkqbXq217Qb5mCzekV3i94vdr2-bHezT2lsyVvxDb+1DSgsaKazmGA2AaZOHNYMLUtBZjKt0DrX7AcHcjjvaw6LudvUhs5eLfm3gFRgoNwx6XhrcuZwuAoVMuMAojMEA0CbV7aIlbcfTX+45xlLrkPdI6Fb2Mhl0yod2T1pNkEP-IjwgglS6eYfjWJOF4Vmh0o14aP+Ci67+Ng4oagJaIwAA4kqGjE+lUkX1TtP2EqTP2yzoEnSLdRc1-Z85FfOYq5IWFl+MWudkA5GfoA5mlkuqK2LvSFWasgGwKVrrUU4o7Yvg7sbGBhRzYal9hRNOXcHZKxAq7GAPY+xdXbBzS2xCoC7UDtGYOI1V4AxKJHaa0d5r8njitYsycFSpyYbbBs9Ee7OzygPfO8haHwKdiXCBYAAFqAxFrWulIG76xgFAtQncVFqNUBoqR-I+6ZT-mAAAPOUOBYCajnTRLYjhlR17A03mDfRqgCwNHGPo+q0gCwzXCMEQIIJNjxF1CgN0nI9jfGSKANUsTIKLG+PogAckqNJFwYCdG3qYXeLEOAAHY3BOBQE4GIEZghwG4gANngBOQwaiYBFAjiTP69RpIdCfi-P2H0syZKVL9SSbMHhf1qD-L8cBmlqJQWgNYATpA1VVIQ0RzD1xqMCZgaylJSZqKDiHeMY1OFTXTKDbM-D8yCLWrKEMjcUCEjUGACRTZrIOKRIeFy8y8FrG2SsuB5Qla1BVhRBZmje6VGXLouRwBO54MwAQhhW0xH7VISzchFiB7UI9oXca9CiGos2Uc9hpy3ERyjjHK5uYbmFiEaWDajDNlvIYlimR9k4UKOBQg0cMBvnonmQCyFztygN1mUeKhKBNiXyVIEzuAqUDzJFeYjl3ZpUAvseUUmaANVyukK4iaXCN6XLGMs4JoTAgFNRixSwZdJYytiEgBIYA7V9ggDKgAUhAcUsq5gxESSANU7SuGdLGd0pozJZI9H0a-U26AszYAQMAO1UA4AQEllAJZ+rRk1E-l+KZ3tvXil+W-dA2a5iBLUsm1N6bM0gLhJ8l0AArH1aBS3xsWXo-V1aU2UDrdAeWnZFFZ0QUycFeCVXqHQbCy8Rt5SIuRUS62aKsGy0xbyihucqHuwUfms6KKV0ktYcc0af1w7Gp4dSsYcc6WJwZSnZl4iCnst6turluUeVKPpHopkQr9VTrrmKvWmDu1zHnWBlAOzkWtLQBAZgIpfBNjQWqndNDPYTILZBwJpLJ7nrXpSnhprb3LXpXcvQ64UTPJyKysxaU3393svoj0yKmg1ows1TNMBazoD3V7SZAYqyhnDGgXDJz8NnKBumTMsdrmkfvXck0Zoawido5+kFkHmTYCMq5P52HpC9trRmwddGgMNz8DpyDHoUMMcyndfcC8FTaZclq0m9nh6jwsy58lRqpogy3hnG16MoApuxs63GIX5SIGDLAYA2Ak3ORGm02+DGpLk0ptTWmxhWb7ueNMuE0W8CmI+dq12IBuBFaHYxkdtcxwVagIGBApi0HQuFNIce6JKyL2NuVmLjXmubunpQ9z89R69bwHxtzc91COfG1AQ17j-OjAKUAA
```
