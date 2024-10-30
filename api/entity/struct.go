package entity

type Neighbourhood struct {
	Neighbourhoodid *int64   `json:"neighbourhoodid,omitempty"`
	Name            *string  `json:"name"`
	Geofence        *string  `json:"geofence"`
	Latitude        *float64 `json:"latitude,omitempty"`
	Longitude       *float64 `json:"longitude,omitempty"`
	Access          *int64   `json:"access,omitempty"`
	Parent          *User    `json:"parent,omitempty"`
}

type Household struct {
	Householdid    *int64   `json:"householdid"`
	Name           *string  `json:"name,omitempty"`
	HeadID         *int64   `json:"headid,omitempty"`
	About          *string  `json:"about,omitempty"`
	ImageURL       *string  `json:"imageurl,omitempty"`
	Latitude       *float64 `json:"latitude,omitempty"`
	Longitude      *float64 `json:"longitude,omitempty"`
	Address        *string  `json:"address,omitempty"`
	GpsProgress    *float64 `json:"gpsprogress,omitempty"`
	LastModifiedTs *int64   `json:"lastModifiedTs,omitempty"`
	Members        []User   `json:"members,omitempty"`
}

type User struct {
	Userid         *int64          `json:"id"`
	Username       *string         `json:"username"`
	Userabout      *string         `json:"about,omitempty"`
	Password       *string         `json:"password,omitempty"`
	Fullname       *string         `json:"fullname,omitempty"`
	Email          *string         `json:"email,omitempty"`
	Phone          *string         `json:"phone,omitempty"`
	ImageURL       *string         `json:"imageurl,omitempty"`
	Authtoken      *string         `json:"authtoken,omitempty"`
	Householdid    *int64          `json:"householdid"`
	Household      *Household      `json:"household,omitempty"`
	LastModifiedTs *int64          `json:"lastModifiedTs,omitempty"`
	Neighbourhoods []Neighbourhood `json:"neighbourhoods,omitempty"`
}

type GpsPayload struct {
	Timezone  *int64   `json:"timezone,omitempty"`
	Latitude  *float64 `json:"latitude"`
	Longitude *float64 `json:"longitude"`
	Frequency *int64   `json:"frequency,omitempty"`
}

type AddToNeighbourhoodRequest struct {
	Neighbourhoodid *int64          `json:"neighbourhoodid"`
	Userid          *int64          `json:"id"`
	Username        *string         `json:"username"`
	Accs            map[int64]int64 `json:"accs"`
}

type Item struct {
	Itemid          *int64           `json:"id,omitempty"`
	Type            *string          `json:"type,omitempty"`
	Name            *string          `json:"name,omitempty"`
	Description     *string          `json:"description,omitempty"`
	Url             *string          `json:"url,omitempty"`
	TargetUserid    *int64           `json:"targetUserId,omitempty"`
	Images          map[int64]string `json:"images,omitempty"`
	Files           map[int64]string `json:"files,omitempty"`
	StartTs         *int64           `json:"startTs,omitempty"`
	EndTs           *int64           `json:"endTs,omitempty"`
	LastModifiedTs  *int64           `json:"lastModifiedTs,omitempty"`
	Neighbourhoodid *int64           `json:"neighbourhoodId,omitempty"`
	Householdid     *int64           `json:"householdId,omitempty"`
	Userid          *int64           `json:"userId,omitempty"`
}

type SyncResponse struct {
	Items        []Item      `json:"items,omitempty"`
	Itemids      []int       `json:"itemIds,omitempty"`
	Users        []User      `json:"users,omitempty"`
	Userids      []int       `json:"userIds,omitempty"`
	Households   []Household `json:"households,omitempty"`
	Householdids []int       `json:"householdIds,omitempty"`
}
