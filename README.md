# OpsNow Employee Manager Application

## Tech Stack Used
Made using:
- Springboot: Backend service
- PostgreSQL: Database service
- TailwindCSS: CSS utility tool
- React Router: Frontend service, frontend routing and base folder structure
- Formik: Out-of-the-box form handling for React
- Font Awesome: Additional styling

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
2. Open terminal on the directory containing frontend repository, build and run it.
   ```shell
   npm run build
   npm run start
   ```
3. Verify the URL in http://localhost:3000 can be accessed.


## How to Use

Simply open the frontend URL in http://localhost:3000, and navigate around the website.

## General Overview

The project's essentially made with modularity in mind. In the springboot code base, the code is splitted to clear packages:
- `core` - where essential and shared code is located.
- `employees` - where the employee-related functionality resides.
- `tiers`- where the tier-related functionality placed.
- `departments` - where the department-related feature can be found.
- `locations` - where the location-related feature bundled together.

Same idea goes for the frontend. This project opts to group closely-related functions and types together and not to split it on the root directory inside `types` folder, for example. However, the frontend codebase is still introduced with clear separation based on their type to introduce layers of abstraction:

1. `apis` folder store the routine to access backend service's REST API. Pages uses these to interact with backend API.
2. `components` folder stores the singular, *atomic* React component.  Pages uses these to construct the UI.
3. `slices` folder stores group of elements, structuring them for further composition.
4. `types` folder stores typescript `type` that is more logical to be defined as its own rather than defined together with functions that uses them.
5. `utils` folder stores helper function.
6. `routes` folder stores the actual pages logic and UI.

## Architectural Design

### Database Design

There are 5 tables, modeled to the CSV format and the logging history requirement:
1. `tiers`
2. `departments`
3. `locations`
4. `employees`
5. `api_call_history`

In the first four tables, they have a unique, natural key:
1. `tiers.code`
2. `departments.code`
3. `locations.code`
4. `employees.no`

Regardless, this project opts to keep using surrogate key (i.e. column `id` as primary key) based on what seemed to be a common practice. Even so, I acknowledge that removing the `id` column and using the `code` column as primary key would be enough, and the features would be working still without issue.

The complete database design can be found within classpath:db/migration.

### Import as mass insert

The feature requires the system to be able to import CSV file. Meaning, there will be a massive amount of data to be processed consecutively.

Naturally, CSV import flow process a lot of data at once. And ORM is not a good fit for this process due to how it inserts entries one by one. In a large number of data, this could take a very long time.

Therefore, different approach is taken. This project implements mass insert manually. Essentially, the entries are batched as a single insert query. This eliminates the repeated round trip and significantly reduce time to process the request.

The hard limit of how many rows can be inserted depends on how many value need to be injected. $2^{15}$ is the limit. But this project uses 30,000 as the upper bound.

### Reusing `Builder` class.

Probably common way to utilize resulting class from the `@Builder` annotation would be like the following.
```java
FooDTO object = FooDTO.builder() // `.builder()` creates the builder object for FooDTO
    // ... Set the values
    .build();
```

For repeated object building, this is kind of waste of memory, due to how builder object is created for every object that needs to be instantiated.

Therefore to reduce the unnecessary memory usage, this project makes attempt to reuse builder object where appropriate:
- The builder object is instantiated locally in method-level, not in service-level. This is to prevent starvation: when multiple requests coming into the service and needing the builder object, they must wait their turn to use the builder object. By making the builder object available per method call, concurrent/parallel requests can run without issue.
- The builder object is reused mainly where repeated processing happens.

### SPA as frontend

The frontend has quite rich interactivity. Since the project inherently interactive as its own, it is decided to make the frontend an SPA (Single Page Application), making it fully handled by the frontend (and eliminates the need for backend-as-frontend). The benefit in this approach is that this project doesn't concern itself with defining which parts are server-side rendered and which are client-side rendered.

### SASS and Tailwind mix

Throughout the code base, one might notice that usage of SASS file and Tailwind (i.e. styling directly on the `.tsx` file) is scattered here and there.

The developer chose to sacrifice uniformity on how styling is done for development speed. Regardless, there is a common reasoning that can be encountered on places where SASS is used, and where Tailwind is used.

- SASS is used as module on components with very clear boundary.
- Tailwind is the opposite: on component that are more prone to change (such as page components), tailwind is used to maximize speed.

### Design Limitation

There are several unresolved limitation due to time constraint.

1. CSV import cannot gracefully detect duplicate rows. This is because all process is done in a single query, and the error from such query is not helpful to pinpoint which row is problematic. Ultimately this leads to the backend outputting unclear error message. This does not help user to figure out which row needs to be fixed.
   
   The plan to resolve this was to run `SELECT` query that retrieves rows that contains certain value. The existence of such rows means that said value cannot be inserted due to duplication. After running the `SELECT` query, the plan was making the backend to pair the erroneous row with relevant error message. This process will return a JSON where the key is the row number from the CSV, and the value is the error(s). Frontend would be able to show this structured information appropriately after.  
2. No source of truth for the style theming. Some CSS rule is copy-pasted from SASS file for speed. This makes the styling prone to inconsistency when change happens. 