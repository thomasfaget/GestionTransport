# GestionTransport
A managing application to handle arrivals and departures of transports (transporting pieces and materials) for a plane factory.

During the summer 2015, I did an intership in a French plane factory Daher.
My job was to devellop, in collaboration with the logistic manager, an application to manage the arrivals and departures of transports in the factory.
The application can contains all the transport information, such as the origin location, the arrival date, content information, or information to trace the reception and billing of the transports.

All this information were stored in a database server and accessible from any computer in the factory. There were also severals accounts to separate privileges of the user.

The application contains 3 tabs :
 - The actuality tab : to handle new transports waiting for validation and billing (showing for all the user)
 - The history tab : showing all the transport history (showing for VIP and ADMIN users)
 - The account tab : to handle the app acounts (only for ADMIN users)
 
 There is 4 types of accounts :
 - PG : Can only see reduced information of transports in actuality tab
 - EXPE : Can see full information of transports in actualy tab + can validate transports
 - VIP : EXPE permissions + can add/remove transports + can bill transports
 - ADMIN : VIP permissions + can add/remove accounts
 
 The server is handle by an H2 database. There is not dedicated server to launch the database server. The server is launched on the computers executing the application.
