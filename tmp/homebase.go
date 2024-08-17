package main

// To determine the home base from the GPS data
func DetermineHomeBaseFromGPSData(gpsData []GPSData) GPSData {
	// Perform clustering
	clusters := DBSCAN(gpsData)

	var homeCluster []GPSData
	maxDuration := 0.0

	// Evaluate each cluster
	for _, cluster := range clusters {
		avgStayDuration := calculateAverageStayDuration(cluster)
		if avgStayDuration > maxDuration {
			maxDuration = avgStayDuration
			homeCluster = cluster
		}
	}

	homeBase := calculateCentroid(homeCluster)
	return homeBase
}

// Calculate the average stay duration in a cluster
func calculateAverageStayDuration(cluster []GPSData) float64 {
	if len(cluster) == 0 {
		return 0
	}

	totalDuration := 0.0
	for i := 1; i < len(cluster); i++ {
		totalDuration += float64(cluster[i].Timestamp - cluster[i-1].Timestamp)
	}

	return totalDuration / float64(len(cluster)-1)
}

// Calculate the centroid of a cluster
func calculateCentroid(cluster []GPSData) GPSData {
	var sumLat, sumLon float64

	for _, data := range cluster {
		sumLat += data.Latitude
		sumLon += data.Longitude
	}

	count := float64(len(cluster))
	return GPSData{
		Latitude:  sumLat / count,
		Longitude: sumLon / count,
	}
}
