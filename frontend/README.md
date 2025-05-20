# SkyAPI Weather Forecast Frontend

A responsive and accessible Next.js application for displaying weather forecast data from the SkyAPI Weather Forecast APIs.

## Features

- **Dashboard**: View current weather and forecasts for any location or your current location
- **Location Management**: Add, edit, and remove locations for weather monitoring
- **Weather Details**: View comprehensive weather information including:
  - Realtime weather conditions
  - Hourly forecasts
  - Daily forecasts (multi-day)
- **Responsive Design**: Fully responsive interface that works on mobile, tablet, and desktop devices
- **Accessibility**: WCAG 2.1 AA compliant with extensive screen reader and keyboard navigation support
- **Theming**: Light and dark mode support with system preference detection
- **User Preferences**: Customizable settings for temperature units and data refresh intervals

## Technology Stack

- **Framework**: [Next.js](https://nextjs.org/) (App Router)
- **Language**: [TypeScript](https://www.typescriptlang.org/)
- **UI Components**: [shadcn/ui](https://ui.shadcn.com/) (Radix UI + Tailwind CSS)
- **Styling**: [Tailwind CSS](https://tailwindcss.com/)
- **Form Handling**: [React Hook Form](https://react-hook-form.com/) with [Zod](https://github.com/colinhacks/zod) validation
- **Data Visualization**: [Recharts](https://recharts.org/)
- **Icons**: [Lucide React](https://lucide.dev/)
- **Testing**: [Jest](https://jestjs.io/) + [React Testing Library](https://testing-library.com/docs/react-testing-library/intro/)

## Prerequisites

- Node.js 18.x or higher
- npm 9.x or higher
- A running instance of the SkyAPI Weather Forecast backend (Spring Boot application)

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/skyapi-weather-frontend.git
   cd skyapi-weather-frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Set up environment variables:
   Create a `.env.local` file in the root directory with the following variables:
   ```
   NEXT_PUBLIC_API_URL=http://localhost:8080
   ```
   Update the URL to match your backend API server address.

4. Start the development server:
   ```bash
   npm run dev
   ```

5. Open [http://localhost:3000](http://localhost:3000) in your browser to view the application.

## Building for Production

1. Build the application:
   ```bash
   npm run build
   ```

2. Start the production server:
   ```bash
   npm start
   ```

## Project Structure

- `src/app`: Next.js application pages and routes
- `src/components`: React components organized by feature
  - `common`: Shared components like Header, Footer, etc.
  - `locations`: Location management components
  - `weather`: Weather display components
  - `settings`: User preferences components
  - `ui`: shadcn/ui components
- `src/lib`: Application utilities and hooks
  - `api`: API client functions
  - `hooks`: Custom React hooks
  - `utils`: Utility functions
- `src/types`: TypeScript type definitions
- `public`: Static assets
- `__tests__`: Test files mirroring the src structure

## API Integration

This frontend application is designed to work with the SkyAPI Weather Forecast APIs. The API client functions are located in `src/lib/api/` and include:

- `locations.ts`: Functions for managing locations
- `realtime.ts`: Functions for fetching realtime weather data
- `hourly.ts`: Functions for hourly forecast data
- `daily.ts`: Functions for daily forecast data
- `full.ts`: Functions for combined weather data

Each API module provides functions for fetching data by IP address or by location code, as well as functions for updating data where applicable.

## Accessibility

This application is designed to be accessible according to WCAG 2.1 AA standards. Key accessibility features include:

- Semantic HTML structure
- Proper keyboard navigation support
- ARIA attributes and roles where appropriate
- Screen reader announcements for dynamic content
- High contrast mode support
- Responsive design for all device sizes

For more details, see the [Accessibility Audit](docs/ACCESSIBILITY.md) document.

## Testing

Run the test suite with:
```bash
npm test
```

To run tests with coverage:
```bash
npm test -- --coverage
```

The project includes the following types of tests:
- Unit tests for components
- Unit tests for utility functions
- Integration tests for hooks and API interactions
- Accessibility tests using jest-axe

## Contributing

1. Fork the repository
2. Create your feature branch: `git checkout -b feature/my-new-feature`
3. Commit your changes: `git commit -am 'Add some feature'`
4. Push to the branch: `git push origin feature/my-new-feature`
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- Weather data provided by SkyAPI Weather Forecast API
- UI components from shadcn/ui and Radix UI
- Icons from Lucide React
- Weather icons inspired by various open-source icon sets