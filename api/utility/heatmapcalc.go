package utility

import (
	"api/entity"
	"strconv"
)

func RetrieveHeatmap(userId string, onlyNight bool) ([]entity.GpsPayload, error) {
	factorStr := strconv.Itoa(GpsPrecisionFactor)
	query := `SELECT 
					ROUND(coordinates_add_numerics_1 / ` + factorStr + `, 6), 
					ROUND(coordinates_add_numerics_2 / ` + factorStr + `, 6), 
					COUNT(*) 				
				FROM coordinates
				WHERE coordinates_add_numerics_0 = ?`

	if onlyNight {
		query += ` AND (
						TIME(ADDTIME(FROM_UNIXTIME(coordinates_data), SEC_TO_TIME(coordinates_add_numerics_3 * 3600))) BETWEEN '` + NightStart + `' AND '23:59:59'
						OR TIME(ADDTIME(FROM_UNIXTIME(coordinates_data), SEC_TO_TIME(coordinates_add_numerics_3 * 3600))) BETWEEN '00:00:00' AND '` + NightEnd + `'
					)`
	}

	query += ` GROUP BY coordinates_add_numerics_1, coordinates_add_numerics_2`

	rows, err := DB.Query(query, userId)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var gpsPayloads []entity.GpsPayload
	for rows.Next() {
		var gpsPayload entity.GpsPayload
		err := rows.Scan(
			&gpsPayload.Latitude,
			&gpsPayload.Longitude,
			&gpsPayload.Frequency,
		)

		if err != nil {
			return nil, err
		}

		gpsPayloads = append(gpsPayloads, gpsPayload)
	}

	return gpsPayloads, nil
}
