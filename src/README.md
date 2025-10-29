# OpsNow Employee Manager Application

## Tech Stack Used
Made using:
- Springboot
- PostgreSQL
- TailwindCSS
- React Router
- Formik
- Font Awesome

## Prerequisite

This project requires Java 21, podman, and podman-compose already installed.

## Installation

### Backend
1. Clone this repository (backend)
2. Open terminal on the directory containing backend repository. Then run the PostgreSQL image:
   ```shell
   podman-compose up db
   ```
3. Set up the .env file. For example,
   ```env
   POSTGRES_DB=podmandb
   POSTGRES_USER=podmanuser
   POSTGRES_PASSWORD=12345678
   
   # Change the end fragment to be the same as POSTGRES_DB value
   SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/podmandb
   ```
4. Run the server using the following command:
   ```shell
   set -a; source .env; set +a; ./gradlew bootRun
   ```

Containerization for the backend server did not get picked up in time due to time constraint. Therefore manual server build using the above steps is needed.

### Frontend
1. Clone the [frontend repository](github.com/mghazian/fe-employee-manager)
4. Open terminal on the directory containing frontend repository, build and run it.
   ```shell
   npm run build
   npm run start
   ```
3Verify the URL in http://localhost:3000/location can be accessed.


## How to Use

Due to constraint of time, the navigation in the frontend is limited. Therefore, **navigation to main pages needs to be entered manually by typing/copy-pasting the URL.**

### Main Dashboard Links

Here is the list of the URL where the rest of the module can be reachable from.

- Location dashboard: http://localhost:3000/location
- Tier dashboard: http://localhost:3000/tier
- Department dashboard: http://localhost:3000/department
- Employee dashboard: http://localhost:3000/employee

### Analytic Links

The following list cannot be accessible yet from anywhere unfortunately, so direct URL access is needed. 

- Salary ranking: http://localhost:3000/employee/salary-ranking
- Cumulative salary per department: http://localhost:3000/employee/cumulative-salary-per-department
- Department by location: http://localhost:3000/employee/department-by-location

## General Overview

The project's essentially made with modularity in mind. In the springboot code base, the code is splitted to clear packages:
- `core` - where essential and shared code is located.
- `employees` - where the employee-related functionality resides.
- `tiers`- where the tier-related functionality placed.
- `departments` - where the department-related feature can be found.
- `locations` - where the location-related feature bundled together.

The same goes for the frontend. This project opts to group closely-related functions and types together and not to split it on the root directory inside `types` folder, for example.

## Architectural Design

### Database Design

The database design can be found within classpath:db/migration.

### Import as mass insert

Naturally, CSV import flow process a lot of data at once.

ORM is not a good fit for this process due to how it inserts entries one by one. In a large number of data, this could take a very long time.

Therefore, different approach is taken. This project implements mass insert manually in the `*MassInsertRepository` interface. Essentially, the entries are batched as a single insert query. This eliminates the repeated round trip and significantly reduce time to process the request.

The hard limit of how many rows can be inserted depends on how many value need to be injected. $2^15$ is the limit. But this project uses 30,000 as the upper bound.