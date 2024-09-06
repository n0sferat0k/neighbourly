sudo systemctl stop api.service
cd /media/neighbourly/ExtremeSSD/neighbourly/api
go build -o api
sudo systemctl start api.service
sudo systemctl status  api.service
