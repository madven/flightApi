# Flights Api

### 1. Creating Models

I created model classes for Cheap and Business flights. ([`CheapFlight`](src/main/java/com/tokigames/selcukc/model/CheapFlight.java), [`BusinessFlight`](src/main/java/com/tokigames/selcukc/model/BusinessFlight.java)). And I created [`Flight`](src/main/java/com/tokigames/selcukc/model/Flight.java) model for aggregating those two.
Lombok annotations are used here.
Because Cheap Flights' departure and arrival times are in Unix time format, I used custom Json deserializer called [`LocalDateTimeFromEpochDeserializer`](src/main/java/com/tokigames/selcukc/helper/LocalDateTimeFromEpochDeserializer.java).

### 2. Fetching Flights

Cheap and Business Flights are fetched from their resources. 
In order to fetch them I created their repositories([`CheapRepository`](src/main/java/com/tokigames/selcukc/repository/CheapRepository.java) and [`BusinessRepository`](src/main/java/com/tokigames/selcukc/repository/BusinessRepository.java)). I used `RestTemplate`'s `exchange` method to make GET requests as well as serialization of responses to corresponding model classes (`ParameterizedTypeReference`).

### 3. Flight Controller 

The api url is `api/flights`. 
- For filtering, there are six request parameters available, namely `departure`, `departureBefore`, `departureAfter`, `arrival`, `arrivalBefore`, `arrivalAfter`. The `departure` and `arrival` parameters are checking the exact match (`.equals()`). As their names imply, the rest of the 4 parameters are provided for date comparison. Time parameters should be provided in milliseconds.
- For sorting and pagination, I used Spring's Pageable class. As an example, if the request is: `localhost:8080/api/flights?page=1&size=10&sort=departureTime,desc`, the `page`, `size` and `sort`(both sort property and sort direction) parameters are all handled with `pageable` object.
- I checked if sort parameters are provided properly, otherwise throwed Bad Request exception. For type safety I created [`OrderBy`](src/main/java/com/tokigames/selcukc/enums/OrderBy.java) enum.
- And lastly, thanks to the Page response entity (`ResponseEntity<Page<Flight>>`), other than the result content, page related informations are provided such as `totalPages`, `totalElements`, `pageSize`, `pageNumber`, `numberOfElements`, etc.

### 4. Flight Service

1. I unified the two fetched results in a single Flight list (unifyFlights method).
2. Filtering is applied on this unified list by the request parameters (filterFlights method).
3. Sorting applied on filtered flights (sortFlights method).
4. And lastly pagination is applied on sorted flights. (pageFlights method)

