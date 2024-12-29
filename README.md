# Stayease
> Develop a application to streamline the room booking process for a hotel management aggregator application using Srping boot, Mysql, spring security

## Endpoints

### Auth
- POST /auth/register - register to application
- POST /auth/login - login to application and generate token

### User
- GET /user - Retrieve a list of all registered user
- GET /user/me - Retrieve the details of a loggedin user
- PUT /user/update-profile - Update detail of a specific user
- DELETE /user/{userId} - Delete a specific user

### Hotel
- GET /hotel - Retrieve a list of all created hotel
- GET /hotel/{hotelId} - Retrieve the details of a specific hotel
- Get /hotel/avalibility/{bookId} - check book avaliable or not
- POST /hotel - Register a new hotel
- PUT /hotel/update/{hotelId} - Update detail of a specific hotel
- DELETE /hotel/{hotelId} - Delete a specific hotel

### Booking
- POST /booking/hotel/{hotelId} - booking a hotel
- GET /booking/{hotelId} - fetch booking detail by booking id
- GET /booking/user - fetch booking history of user
- GET /booking/ - fetch all booking
- PUT /booking/cancel/{bookingId} - cancel booking 

## Usage
> this application is implemented on the basis of JWT based Authrization, so you should have authrization header which contains jwt token  to secure private request. Also implemented Authrization (ADMIN, CUSTOMER, HOTELMANAGER)

## Postman Collection
[Stayease.postman_collection.json](https://github.com/user-attachments/files/18268786/Stayease.postman_collection.json)
