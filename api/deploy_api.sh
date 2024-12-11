cd /home/neighbourly/server/api
sudo systemctl stop api.service
sudo go build -o api
sudo systemctl start api.service
sudo systemctl status  api.service
