# asterisk-csp

[![Join the chat at https://gitter.im/jonas-koeritz/asterisk-csp](https://badges.gitter.im/jonas-koeritz/asterisk-csp.svg)](https://gitter.im/jonas-koeritz/asterisk-csp?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg?style=flat)

A CSTA III XML service provider for Asterisk using the Asterisk Manager Interface

## History
Following the succesful implementation of a CSTA Service Provider in Node.js for experimental use, this will be an attempt to create a more complete, more universal and more robust implementation in Java.
There are several pitfalls when working with Asterisk as a "Switching Function". The ECMA standard is most suitable for circuit switched telephony.

This project is still in its very beginnings but given the already working (crudely done, highly experimental, incomplete and unstable) implementation progress should be visible and hopefully usable soon.

## Project Goals
The main goal of this project is to be able to control Asterisk devices via a CTI Application, my testing will be done mostly using Xphone UC by C4B. Feel free to leave a note as an issue if you encounter problems using other client software later in the development process.
Asterisk must stay untouched! No patches or changes should be necessary to use this implementation.

## Roadmap
1. Create an usable object model (based on ECMA TR-88) to represent an Asterisk server as a Switching Domain
2. Make the key objects serializable for use as CSTA-XML Events/Requests/Responses
3. Handle TCP client connections, establish and keep-alive CSTA sessions
4. Connect to Asterisk and process AMI events to update the state of the object model accordingly
5. Generate suitable CSTA events for all conditions
6. Process CSTA requests and control Asterisk accordingly

## Contributing
Anybody may file an issue or send pull requests. Any help is greatly appreciated.
