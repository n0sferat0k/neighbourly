package utility

import (
	"api/entity"
	"errors"
	"math"
)

const EarthRadius = 6371000               // Radius of Earth in meters
const MaxHouseholdClusterWidthMeters = 50 // 50 meters
const MaxNeighbourhoodSizeMeters = 10000  // 10 km
const MinClusterDiversity = 3
const GpsSampleTarget = 100
const GpsPrecisionFactor = 1000000
const NightStart = "20:00:00"
const NightEnd = "09:00:00"

// haversineDistance calculates the distance between two GPS coordinates in meters.
func HaversineDistance(lat1, lon1, lat2, lon2 float64) float64 {
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

func PointInPolygon(lon, lat float64, polygon [][2]float64) bool {
	oddNodes := false
	j := len(polygon) - 1

	// Iterate over all edges of the polygon
	for i := 0; i < len(polygon); i++ {
		// Check if the point is within the bounds of the edge
		if polygon[i][0] < lon && polygon[j][0] >= lon || polygon[j][0] < lon && polygon[i][0] >= lon {
			// Check if the point is to the left of the edge
			if polygon[i][1]+(lon-polygon[i][0])/(polygon[j][0]-polygon[i][0])*(polygon[j][1]-polygon[i][1]) < lat {
				// Toggle the oddNodes flag
				oddNodes = !oddNodes
			}
		}
		j = i
	}
	// Return true if the number of intersections is odd
	return oddNodes
}

func FindMaxSpreadAndCenter(gpsData [][2]float64) (float64, [2]float64) {
	var mostEasternPoint [2]float64 = gpsData[0]
	var mostWesternPoint [2]float64 = gpsData[0]
	var mostNorthernPoint [2]float64 = gpsData[0]
	var mostSouthernPoint [2]float64 = gpsData[0]
	var center [2]float64

	for i := 1; i < len(gpsData); i++ {
		if gpsData[i][0] > mostEasternPoint[0] {
			mostEasternPoint = gpsData[i]
		}

		if gpsData[i][0] < mostWesternPoint[0] {
			mostWesternPoint = gpsData[i]
		}

		if gpsData[i][1] > mostNorthernPoint[1] {
			mostNorthernPoint = gpsData[i]
		}

		if gpsData[i][1] < mostSouthernPoint[1] {
			mostSouthernPoint = gpsData[i]
		}
	}

	center[0] = (mostEasternPoint[0] + mostWesternPoint[0]) / 2
	center[1] = (mostNorthernPoint[1] + mostSouthernPoint[1]) / 2

	return math.Max(
		HaversineDistance(mostEasternPoint[1], mostEasternPoint[0], mostWesternPoint[1], mostWesternPoint[0]),
		HaversineDistance(mostNorthernPoint[1], mostNorthernPoint[0], mostSouthernPoint[1], mostSouthernPoint[0]),
	), center
}

// CalculateCandidate finds the likely home location by identifying the largest cluster of GPS points
// within a 50-meter radius and calculating the weighted average coordinates based on frequency.
func FindLargestCluserLocation(gpsData []entity.GpsPayload) (entity.GpsPayload, error) {
	var bestCluster []entity.GpsPayload
	maxClusterSize := 0

	// Find the largest cluster of GPS coordinates within the 50-meter radius
	for i := 0; i < len(gpsData); i++ {
		if gpsData[i].Latitude == nil || gpsData[i].Longitude == nil || gpsData[i].Frequency == nil {
			continue // Skip if essential fields are missing
		}

		cluster := []entity.GpsPayload{gpsData[i]}

		for j := 0; j < len(gpsData); j++ {
			if i != j && gpsData[j].Latitude != nil && gpsData[j].Longitude != nil {
				if HaversineDistance(*gpsData[i].Latitude, *gpsData[i].Longitude, *gpsData[j].Latitude, *gpsData[j].Longitude) <= MaxHouseholdClusterWidthMeters {
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
		return entity.GpsPayload{}, errors.New("Not enough diversity in GPS data")
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
		return entity.GpsPayload{}, errors.New("Not enough diversity in GPS data")
	}

	averageLat := sumLat / totalFrequency
	averageLon := sumLon / totalFrequency

	return entity.GpsPayload{
		Latitude:  &averageLat,
		Longitude: &averageLon,
	}, nil
}
