// tailwind.config.js
const defaultTheme = require('tailwindcss/defaultTheme')

module.exports = {
  purge: ["./src/**/*.{js,jsx,ts,tsx}", "./public/index.html"],
  darkMode: false, // or 'media' or 'class'
  theme: {
    extend: {},
    fontFamily: {
      sans: ['OpenSans','Open Sans', 'sans-serif', ...defaultTheme.fontFamily.sans]
    },
    minHeight: {
      ...defaultTheme.minHeight,
      "20": "5rem",
    }

  },
  variants: {
    extend: {},
  },
  plugins: [],
};
