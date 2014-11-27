monopoly
========

A multi-player Monopoly game using client-server architecture. Server is a stand-alone entity which requires someone to start it. All other players use the client program to connect to the server and play the game.

## Assumptions & Requirements:
•	The working system should have the Java Run-time Environment.
•	Network facilities (drivers and hardware) 
•	Player need not have the server application.
•	Server is a standalone application
•	Only who starts the server application can set the game settings.

## Goals:
•	Keep it simple and intuitive.
•	User friendly.
•	Optimum memory usage.
•	Network traffic should be less.

## Development methods:
It is a client-server application. All the players (systems in the network) are clients, and the application that connects to the clients together is the server.

## Architectural Strategies
Two-tier Architecture

## System Architecture
Server - business logic
Client - user interface

## Detailed System Design
### Classification
System is a Client-Server application using Sockets and Streams.

### Definition
As per the design, the application uses the two-tier architecture. And importantly, It works with a very thin client layer. The server holds the complete business logic to handle any number of players, while the client takes care of the presentation layer based on the messages from the server.

### Responsibilities
Client:
•	Good Presentation Layer.
•	Establish connection with Server.
•	Send and receive data from/to other clients.
Server:
•	Maintain the connection with clients.
•	Transport messages to the correct client.

