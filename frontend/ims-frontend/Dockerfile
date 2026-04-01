# Stage 1: Build
FROM node:20-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build -- --configuration production

# Stage 2: Serve
FROM nginx:1.27-alpine
COPY --from=builder /app/dist/ims-frontend/browser /usr/share/nginx/html
# Template is processed at startup — API_URL is injected via docker-compose environment
COPY nginx.conf.template /etc/nginx/conf.d/default.conf.template
EXPOSE 10000
ENV PORT=10000
# envsubst replaces ${API_URL} and ${PORT}, leaving nginx variables like $host untouched
CMD ["/bin/sh", "-c", "envsubst '${API_URL} ${PORT}' < /etc/nginx/conf.d/default.conf.template > /etc/nginx/conf.d/default.conf && nginx -g 'daemon off;'"]
