/**
 * The class is the main responsible the main page function
 *  @author Gulmira Kanalinova
 *  @version 1.0
 *  December 5, 2020
 */
package com.example.cst2335_graphicalinterfaceprogramming;

class Track {
	String id;
	String artistName;
	String trackName;

	public Track(String id, String artistName, String trackName) {
		this.id = id;
		this.artistName = artistName;
		this.trackName = trackName;
	}

	@Override
	public String toString() {
		return artistName + " - " + trackName;
	}
}
