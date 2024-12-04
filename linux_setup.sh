///update OS
sudo apt update
sudo apt full-upgrade -y
sudo apt-get update
sudo apt-get upgrade -y

//install apache
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

//install remote desktop for PI
sudo apt install rpi-connect
rpi-connect signin

//MOVE APACHE SHIT TO THE SSD
cd /etc/apache2
sudo nano apache2.conf
	<Directory /media/neighbourly/ExtremeSSD/neighbourly/www>
        Options Indexes FollowSymLinks
        AllowOverride None
        Require all granted
	</Directory>
cd /etc/apache2/sites-available
sudo nano 000-default.conf
	DocumentRoot /media/neighbourly/ExtremeSSD/neighbourly/www
sudo systemctl restart apache2
sudo chown -R www-data:www-data /media/neighbourly/ExtremeSSD/neighbourly/www
sudo chmod -R 755 /media

												//MOVE DB SHIT TO THE SSD
												sudo systemctl stop mariadb
												sudo cp -R /var/lib/mysql /media/neighbourly/ExtremeSSD/neighbourly/db
												sudo nano /etc/mysql/mariadb.conf.d/50-server.cnf
													[mysqld]
													datadir=/media/neighbourly/ExtremeSSD/neighbourly/db
													[client]
													socket=/media/neighbourly/ExtremeSSD/neighbourly/db/mysql.sock													
												sudo chown -R mysql:mysql /media/neighbourly/ExtremeSSD/neighbourly/db
												sudo chmod -R 755 /media/neighbourly/ExtremeSSD/neighbourly/db
												sudo systemctl start mariadb

												sudo mkdir -p /etc/systemd/system/mariadb.service.d
												sudo nano /etc/systemd/system/mariadb.service.d/override.conf
													[Service]
													ExecStartPre=/bin/sleep 10
												sudo systemctl daemon-reload
												sudo systemctl restart mariadb

//INIT AND BUILD GO MODULE
sudo go mod init main
sudo go get github.com/go-sql-driver/mysql
sudo go get github.com/gorilla/mux
sudo go get golang.org/x/crypto/bcrypt
sudo go get github.com/eclipse/paho.mqtt.golang
sudo go build -o api

sudo nano /etc/systemd/system/api.service
	[Unit]
	Description=Neighbourly Go Api
	After=network.target media-neighbourly-ExtremeSSD.mount
	Requires=media-neighbourly-ExtremeSSD.mount

	[Service]
	ExecStartPre=/bin/sleep 10
	ExecStart=/media/neighbourly/ExtremeSSD/neighbourly/api/api
	WorkingDirectory=/media/neighbourly/ExtremeSSD/neighbourly/api
	StandardOutput=inherit
	StandardError=inherit
	Restart=always
	User=neighbourly

	[Install]
	WantedBy=multi-user.target
sudo systemctl daemon-reload
sudo systemctl enable api.service
sudo systemctl start api.service
sudo systemctl status api.service

//INSTALL MQTT
sudo apt-get update
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
	persistence_location /media/neighbourly/ExtremeSSD/neighbourly/mqtt/
	allow_anonymous false
	password_file /etc/mosquitto/passwd
	acl_file /etc/mosquitto/acl
	listener 1883
sudo systemctl restart mosquitto
sudo chown -R mosquitto:mosquitto /media/neighbourly/ExtremeSSD/neighbourly/mqtt/
sudo chmod -R 755 /media/neighbourly/ExtremeSSD/neighbourly/mqtt/