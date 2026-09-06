# Vehicle Manager

Vehicle Manager is a native Android application designed to help users manage and track their vehicles efficiently. The app provides tools for recording fuel refuels, maintenance services, and vehicle details, along with visual summaries to better understand usage and expenses over time.

## Features

- **Vehicle Management**: Add and edit vehicle information including brand, model, license plate, and odometer readings.
- **Fuel Tracking**: Record fuel refuels with odometer readings, price per liter, fuel quantity, and automatic total cost calculation.
- **Maintenance Tracking**: Log maintenance services performed on vehicles with associated costs and odometer readings.
- **Dashboard & Analytics**: View monthly, quarterly, semi-annual, and annual summaries with charts and insights.
- **Settings & Reminders**: Configure app theme, data export/import, and reminder preferences for fuel and maintenance notifications.

## Technology Stack

- Kotlin with Jetpack Compose for UI
- Material 3 design system
- MVVM architecture with Clean Architecture layers
- Hilt for dependency injection
- Room for local database persistence
- DataStore for user preferences
- Coroutines and Flow for asynchronous operations

## Project Structure

The project follows a layered architecture:

- `core/`: Shared domain models, repositories, and UI components
- `feature/`: Feature modules (vehicles, fuel, maintenance, settings, dashboard)
- `app/`: Application entry point and dependency injection setup
