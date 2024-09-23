package main

import "strconv"

func RetrieveHeatmap(userId string, onlyNight bool) ([]GpsPayload, error) {
	factorStr := strconv.Itoa(gpsPrecisionFactor)
	query := `SELECT coordinates_add_numerics_1 / ` + factorStr + `, coordinates_add_numerics_2 / ` + factorStr + `, COUNT(*) FROM coordinates
				WHERE coordinates_add_numerics_0 = ?`

	if onlyNight {
		query += ` AND (
						TIME(ADDTIME(FROM_UNIXTIME(coordinates_data), SEC_TO_TIME(coordinates_add_numerics_3 * 3600))) BETWEEN '` + nightStart + `' AND '23:59:59'
						OR TIME(ADDTIME(FROM_UNIXTIME(coordinates_data), SEC_TO_TIME(coordinates_add_numerics_3 * 3600))) BETWEEN '00:00:00' AND '` + nightEnd + `'
					)`
	}

	query += ` GROUP BY coordinates_add_numerics_1, coordinates_add_numerics_2`

	rows, err := db.Query(query, userId)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var gpsPayloads []GpsPayload
	for rows.Next() {
		var gpsPayload GpsPayload
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
