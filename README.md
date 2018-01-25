# GestionTransport
A managing application to handle arrivals and departures of transports (transporting pieces and materials) for a plane factory.

During the summer 2015, I did an intership in a French plane factory Daher.
My job was to devellop, in collaboration with the logistic manager, two management software. One of this software is develloped to manage the arrivals and departures of transports in the factory.
The application can contains all the transport information, such as the origin location, the arrival date, content information, or information to trace the reception and billing of the transports.

All this information were stored in a database server and accessible from any computer in the factory. There were also several accounts to separate privileges of the users.

The application contains 3 tabs :
 - The actuality tab : to handle new transports waiting for validation and billing (showing for all the user)
 - The history tab : showing all the transport history (showing for VIP and ADMIN users)
 - The account tab : to handle the app acounts (only for ADMIN users)
 
 There are 4 types of accounts :
 - PG : Can only see reduced information of transports in actuality tab
 - EXPE : Can see full information of transports in actualy tab + can validate transports
 - VIP : EXPE permissions + can add/remove transports + can bill transports
 - ADMIN : VIP permissions + can add/remove accounts
 
The server is handled by an H2 database. There is any dedicated server to launch the database server. The server is launched on the computers executing the application.

Multiple users can be connected at the same time with the database. Indeed, each modification by an user result in a update of the database and the interface of all the connected users.

The software executable ware localized in an unique directory of the network of the factory. So each user must have the permissions to access the directory. The software supports multiple instance of itseft, to have several users at the same time.

The avantage of this software is to be easily implemented in the factory, because there is any dedicated server. The software location just need to be in the network of the company and be accessible by all the users. 

The software doesn't need any absolute path to work correctlty. So it can be moved easily for a directory to another in the network.
