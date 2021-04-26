// tailwind.config.js
const defaultTheme = require('tailwindcss/defaultTheme')

module.exports = {
  purge: ["./src/**/*.{js,jsx,ts,tsx}", "./public/index.html"],
  darkMode: false, // or 'media' or 'class'
  theme: {
    extend: {
      borderWidth: {
        '3': '3px',
      }
    },
    minHeight: {
      ...defaultTheme.minHeight,
      "20": "5rem",
    }

  },
  variants: {
    extend: {
      visibility: ['group-hover']
    },
  },
  plugins: [],
};
