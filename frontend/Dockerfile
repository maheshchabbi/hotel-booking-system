# Step 1: Build the React app
FROM node:alpine3.12 
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
EXPOSE 3000
CMD [ "npm", "start" ]



