/*
 *   Nuclear Plants Map
 *   http://casmi.github.com/
 *   Copyright (C) 2011, Xcoo, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package plantsmap.view;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import plantsmap.data.Plant;
import casmi.parser.CSV;

/**
 * Load power plant data from csv file
 * 
 * @author Y. Ban
 *
 */
public class PowerPlantLoader {
	
	public static List<Plant> load(URL csvfile){

		List<Plant> result = new ArrayList<Plant>();
        
        try {
        	CSV csv = new CSV(csvfile);
        	
        	String[] row = null;
        	
            while ((row = csv.readLine()) != null) {
            	String name = row[0];
            	String[] capacityAry = row[1].split(",");
            	double capacity = Double.valueOf(capacityAry[0]).doubleValue()*1000 + Double.valueOf(capacityAry[1]).doubleValue();
            	String country = row[2];
            	double latitude = Double.valueOf(row[3]).doubleValue();
            	double longitude = Double.valueOf(row[4]).doubleValue();
            	
            	Plant plant = new Plant(name, capacity, country, latitude, longitude);
            	
            	if(!result.contains(plant)){
            		result.add(plant);
            	}
            }
            
            csv.close();
            
        } catch (IOException e) {
        	e.printStackTrace();
        }

        return result;
	}
	
}
