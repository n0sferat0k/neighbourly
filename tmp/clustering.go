package main

import (
	"math"

	"github.com/golang/geo/s2"
)

// Structure to represent a GPS point with S2 CellId for clustering
type GPSPoint struct {
	Data   GPSData
	CellID s2.CellID
}

// Parameters for DBSCAN
const (
	Epsilon = 10.0 // distance tolerance (meters)
	MinPts  = 5    // minimum number of points to form a cluster
)

// Haversine function to calculate distance between two lat/lng points in meters
func haversine(lat1, lon1, lat2, lon2 float64) float64 {
	const R = 6371000 // Earth radius in meters
	dLat := (lat2 - lat1) * math.Pi / 180.0
	dLon := (lon2 - lon1) * math.Pi / 180.0
	a := math.Sin(dLat/2)*math.Sin(dLat/2) +
		math.Cos(lat1*math.Pi/180.0)*math.Cos(lat2*math.Pi/180.0)*
			math.Sin(dLon/2)*math.Sin(dLon/2)
	c := 2 * math.Atan2(math.Sqrt(a), math.Sqrt(1-a))
	return R * c
}

// Function to perform DBSCAN clustering
func DBSCAN(points []GPSData) [][]GPSData {
	var clusters [][]GPSData
	visited := make(map[int]bool)
	noise := make([]int, 0)

	for i := range points {
		if !visited[i] {
			visited[i] = true
			neighbors := regionQuery(points, i)

			if len(neighbors) < MinPts {
				noise = append(noise, i)
			} else {
				newCluster := []GPSData{}
				clusters = append(clusters, expandCluster(points, newCluster, i, neighbors, visited))
			}
		}
	}

	return clusters
}

// Expand cluster algorithm for DBSCAN
func expandCluster(points []GPSData, cluster []GPSData, index int, neighbors []int, visited map[int]bool) []GPSData {
	cluster = append(cluster, points[index])

	for i := 0; i < len(neighbors); i++ {
		nIndex := neighbors[i]
		if !visited[nIndex] {
			visited[nIndex] = true
			newNeighbors := regionQuery(points, nIndex)
			if len(newNeighbors) >= MinPts {
				neighbors = append(neighbors, newNeighbors...)
			}
		}

		includeInCluster := true
		for _, p := range cluster {
			if p.ID == points[nIndex].ID {
				includeInCluster = false
				break
			}
		}
		if includeInCluster {
			cluster = append(cluster, points[nIndex])
		}
	}

	return cluster
}

// Region query for finding neighbors in DBSCAN
func regionQuery(points []GPSData, index int) []int {
	neighbors := make([]int, 0)
	for i := range points {
		if i != index && haversine(points[i].Latitude, points[i].Longitude, points[index].Latitude, points[index].Longitude) <= Epsilon {
			neighbors = append(neighbors, i)
		}
	}
	return neighbors
}
