//install remote desktop for PI
sudo apt install rpi-connect
rpi-connect signin

///update OS
sudo apt update
sudo apt full-upgrade -y
sudo apt-get update
sudo apt-get upgrade -y

//install go and apache
sudo apt install golang-go -y
sudo apt install apache2 -y
sudo systemctl enable apache2
sudo systemctl start apache2

//install DB and DB admin
sudo apt install php libapache2-mod-php php-mysql -y
sudo apt-get install mariadb-server php-mysql -y
sudo apt install phpmyadmin -y
sudo mysql -u root
	ALTER USER 'root'@'localhost' IDENTIFIED BY 'qwerty1234';
	FLUSH PRIVILEGES;
manually create a db called neighbourly

//MOVE APACHE SHIT TO THE SSD
cd /etc/apache2
sudo nano apache2.conf
	<Directory /home/neighbourly/server/www>
        Options Indexes FollowSymLinks
        AllowOverride None
        Require all granted
	</Directory>
cd /etc/apache2/sites-available
sudo nano 000-default.conf
	DocumentRoot /home/neighbourly/server/www
sudo systemctl restart apache2
//copy files over before running chown
sudo chown -R www-data:www-data /home/neighbourly/server/www
sudo chmod -R 777 /home

//INSTALL MQTT
sudo apt-get install -y mosquitto mosquitto-clients
sudo systemctl enable mosquitto
sudo systemctl start mosquitto
sudo mosquitto_passwd -c /etc/mosquitto/passwd neighbourly	//leave out the -c for additional users
	password: Tizenegy11
sudo nano /etc/mosquitto/acl
	user neighbourly
	topic readwrite #
sudo nano /etc/mosquitto/mosquitto.conf
	persistence true
	persistence_location /home/neighbourly/server/mqtt/
	allow_anonymous false
	password_file /etc/mosquitto/passwd
	acl_file /etc/mosquitto/acl
	listener 1883
sudo chown -R mosquitto:mosquitto /home/neighbourly/server/mqtt/
sudo chmod -R 777 /home
sudo systemctl restart mosquitto


//INIT AND BUILD GO MODULE
sudo go mod init main
sudo go get github.com/go-sql-driver/mysql
sudo go get github.com/gorilla/mux
sudo go get golang.org/x/crypto/bcrypt
sudo go get github.com/eclipse/paho.mqtt.golang
sudo go get github.com/nfnt/resize
sudo go run .
sudo go build -o api

sudo nano /etc/systemd/system/api.service
	[Unit]
	Description=Neighbourly Go Api
	After=network.target
	mariadb.service
	Requires=mariadb.service

	[Service]	
	ExecStartPre=/bin/sleep 20
	ExecStart=/home/neighbourly/server/api/api
	WorkingDirectory=/home/neighbourly/server/api
	StandardOutput=inherit
	StandardError=inherit
	Restart=always
	User=neighbourly
	Group=neighbourly

	[Install]
	WantedBy=multi-user.target
sudo systemctl daemon-reload
sudo systemctl enable api.service
sudo systemctl start api.service
sudo systemctl status api.service

