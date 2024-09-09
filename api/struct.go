package main

type Neighbourhood struct {
	Neighbourhoodid *int64  `json:"neighbourhoodid"`
	Name            *string `json:"name"`
	Geofence        *string `json:"geofence"`
	Access          *int64  `json:"access"`
	Parent          *User   `json:"parent"`
}

type Household struct {
	Householdid *int64   `json:"householdid"`
	Name        *string  `json:"name"`
	About       *string  `json:"about"`
	ImageURL    *string  `json:"imageurl"`
	HeadID      *int64   `json:"headid"`
	Latitude    *float64 `json:"latitude"`
	Longitude   *float64 `json:"longitude"`
	Address     *string  `json:"address"`
}

type User struct {
	Userid         *int64          `json:"id"`
	Username       *string         `json:"username"`
	Userabout      *string         `json:"about"`
	Password       *string         `json:"password,omitempty"`
	Fullname       *string         `json:"fullname"`
	Email          *string         `json:"email"`
	Phone          *string         `json:"phone"`
	ImageURL       *string         `json:"imageurl,omitempty"`
	Authtoken      *string         `json:"authtoken,omitempty"`
	Household      *Household      `json:"household,omitempty"`
	Neighbourhoods []Neighbourhood `json:"neighbourhoods,omitempty"`
}
