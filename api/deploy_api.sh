#!/bin/bash

# Define variables
SERVICE_NAME="api.service"
PROJECT_DIR="/media/neighbourly/ExtremeSSD/neighbourly/api"
EXECUTABLE_NAME="api"

# Stop the existing service
# echo "Stopping the existing service..."
# sudo systemctl stop $SERVICE_NAME

# Navigate to the project directory
# cd $PROJECT_DIR

# Pull the latest changes from the repository
# echo "Pulling the latest changes from the repository..."
# git pull origin main

# Build the Go application
# echo "Building the Go application..."
# go build -o $EXECUTABLE_NAME

# Restart the service
# echo "Starting the service..."
# sudo systemctl start $SERVICE_NAME

# Check the status of the service
# echo "Checking the status of the service..."
# sudo systemctl status $SERVICE_NAME