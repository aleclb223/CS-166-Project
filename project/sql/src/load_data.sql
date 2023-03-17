COPY Users
FROM 'users.csv' 
WITH DELIMITER ',' CSV HEADER;
ALTER SEQUENCE users_userID_seq RESTART 101; 

COPY Hotel
FROM 'hotels.csv'
WITH DELIMITER ',' CSV HEADER;   

COPY Rooms
FROM 'rooms.csv'
WITH DELIMITER ',' CSV HEADER;

COPY MaintenanceCompany
FROM 'company.csv'
WITH DELIMITER ',' CSV HEADER;

COPY RoomBookings
FROM 'bookings.csv'
WITH DELIMITER ',' CSV HEADER;
ALTER SEQUENCE RoomBookings_bookingID_seq RESTART 501; 

COPY RoomRepairs
FROM 'roomRepairs.csv'
WITH DELIMITER ',' CSV HEADER;
ALTER SEQUENCE roomRepairs_repairID_seq RESTART 11;

COPY RoomRepairRequests
FROM 'roomRepairRequests.csv'
WITH DELIMITER ',' CSV HEADER;
ALTER SEQUENCE roomRepairRequests_requestNumber_seq RESTART 11;

COPY RoomUpdatesLog
FROM 'roomUpdatesLog.csv'
WITH DELIMITER ',' CSV HEADER;
ALTER SEQUENCE roomUpdatesLog_updateNumber_seq RESTART 51;
