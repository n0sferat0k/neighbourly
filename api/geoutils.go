package main

import (
	"errors"
	"math"
	"strconv"
)

const EarthRadius = 6371000 // Radius of Earth in meters
const MinClusterDiversity = 3
const gpsSampleTarget = 100
const gpsPrecisionFactor = 1000000
const nightStart = "20:00:00"
const nightEnd = "09:00:00"

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

// haversineDistance calculates the distance between two GPS coordinates in meters.
func haversineDistance(lat1, lon1, lat2, lon2 float64) float64 {
	lat1Rad := lat1 * math.Pi / 180
	lon1Rad := lon1 * math.Pi / 180
	lat2Rad := lat2 * math.Pi / 180
	lon2Rad := lon2 * math.Pi / 180

	dLat := lat2Rad - lat1Rad
	dLon := lon2Rad - lon1Rad

	a := math.Sin(dLat/2)*math.Sin(dLat/2) +
		math.Cos(lat1Rad)*math.Cos(lat2Rad)*math.Sin(dLon/2)*math.Sin(dLon/2)
	c := 2 * math.Atan2(math.Sqrt(a), math.Sqrt(1-a))

	return EarthRadius * c
}

// CalculateCandidate finds the likely home location by identifying the largest cluster of GPS points
// within a 50-meter radius and calculating the weighted average coordinates based on frequency.
func CalculateCandidateHouseholdLocation(gpsData []GpsPayload) (GpsPayload, error) {
	const MaxDistance = 50 // 50 meters

	var bestCluster []GpsPayload
	maxClusterSize := 0

	// Find the largest cluster of GPS coordinates within the 50-meter radius
	for i := 0; i < len(gpsData); i++ {
		if gpsData[i].Latitude == nil || gpsData[i].Longitude == nil || gpsData[i].Frequency == nil {
			continue // Skip if essential fields are missing
		}

		cluster := []GpsPayload{gpsData[i]}

		for j := 0; j < len(gpsData); j++ {
			if i != j && gpsData[j].Latitude != nil && gpsData[j].Longitude != nil {
				if haversineDistance(*gpsData[i].Latitude, *gpsData[i].Longitude, *gpsData[j].Latitude, *gpsData[j].Longitude) <= MaxDistance {
					cluster = append(cluster, gpsData[j])
				}
			}
		}

		clusterSize := 0
		for _, point := range cluster {
			if point.Frequency != nil {
				clusterSize += int(*point.Frequency)
			}
		}

		if clusterSize > maxClusterSize {
			maxClusterSize = clusterSize
			bestCluster = cluster
		}
	}

	//If the best cluster is not diverse enough, return an error
	if len(bestCluster) < MinClusterDiversity {
		return GpsPayload{}, errors.New("Not enough diversity in GPS data")
	}

	// Calculate the weighted average coordinates of the best cluster
	var sumLat, sumLon, totalFrequency float64
	for _, point := range bestCluster {
		if point.Latitude != nil && point.Longitude != nil && point.Frequency != nil {
			sumLat += *point.Latitude * float64(*point.Frequency)
			sumLon += *point.Longitude * float64(*point.Frequency)
			totalFrequency += float64(*point.Frequency)
		}
	}

	// Handle case where no valid cluster is found
	if totalFrequency == 0 {
		return GpsPayload{}, errors.New("Not enough diversity in GPS data")
	}

	averageLat := sumLat / totalFrequency
	averageLon := sumLon / totalFrequency

	return GpsPayload{
		Latitude:  &averageLat,
		Longitude: &averageLon,
	}, nil
}
