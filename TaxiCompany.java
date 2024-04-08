package taxify;

import java.util.List;

public class TaxiCompany implements ITaxiCompany, ISubject {
    private String name;
    private List<IUser> users;
    private List<IVehicle> vehicles;
    private int totalServices;
    private IObserver observer;
    
    public TaxiCompany(String name, List<IUser> users, List<IVehicle> vehicles) {
        this.name = name;
        this.users = users;
        this.vehicles = vehicles;        
        this.totalServices = 0;
        
        for (IUser user : this.users) {
            user.setCompany(this);
        }
        
        for (IVehicle vehicle : this.vehicles) {
            vehicle.setCompany(this);
        }
    }
    
    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getTotalServices() {
        return this.totalServices;
    }
        
    @Override
    public boolean provideService(int user, VehicleType vehicleType, SoundType soundType) {
        int userIndex = findUserIndex(user);

        ILocation origin, destination;

        origin = ApplicationLibrary.randomLocation();
        destination = ApplicationLibrary.randomLocation(origin);

        int vehicleIndex = findFreeVehicle(vehicleType, origin);
        
        // if there is a free vehicle, assign a random pickup and drop-off location to the new service
        // the distance between the pickup and the drop-off location should be at least 3 blocks
        
        if (vehicleIndex != -1) {

            IVehicle vehicle = this.vehicles.get(vehicleIndex);

            vehicle.addPassengers();

            // update the user status
                       
            this.users.get(userIndex).setService(true);
            
            // create a service with the user, the pickup and the drop-off location

            IService service = new Service(this.users.get(userIndex), origin, destination, vehicleType, soundType);
            
            // assign the new service to the vehicle
            
            this.vehicles.get(vehicleIndex).pickService(service);            
             
            notifyObserver("User " + this.users.get(userIndex).getId() + " requests a service from " + service.toString() + ", the ride is assigned to " +
                           this.vehicles.get(vehicleIndex).getClass().getSimpleName() + " " + this.vehicles.get(vehicleIndex).getId() + " at location " +
                           this.vehicles.get(vehicleIndex).getLocation().toString());
            
            // update the counter of services
            
            this.totalServices++;
            
            return true;
        }
        
        return false;
    }

    @Override
    public void arrivedAtPickupLocation(IVehicle vehicle) {
        // notify the observer a vehicle arrived at the pickup location 
        notifyObserver(String.format("%-8s",vehicle.getClass().getSimpleName()) + vehicle.getId() + " has arrived to the pickup lcation.");      
        
    }
  
    @Override
    public void arrivedAtDropoffLocation(IVehicle vehicle) {
        // a vehicle arrives at the drop-off location

        if(vehicle.getPassengers() == 2) {
            IService service = vehicle.getService();         
        } else {
            IService service = vehicle.getSharedService();       
        }
        int user = service.getUser().getId();
        int userIndex = findUserIndex(user);
    
        // the taxi company requests the user to rate the service, and updates its status

        this.users.get(userIndex).rateService(service);
        this.users.get(userIndex).setService(false);

        // update the counter of services
        
        this.totalServices--;    
        
        notifyObserver(String.format("%-8s",vehicle.getClass().getSimpleName()) + vehicle.getId() + " drops off user " + user);
       
    }
        
    @Override
    public void addObserver(IObserver observer) {
        this.observer = observer; 
    }
    
    @Override
    public void notifyObserver(String message) {
        this.observer.updateObserver(message);
    }
    
    private int findFreeVehicle(VehicleType vehicleType, ILocation origin) {
        int index = -1;

        if(vehicleType == VehicleType.PINK) {

            do {
            
                index = ApplicationLibrary.rand(this.vehicles.size());
                
            } while (!this.vehicles.get(index).isFree() && this.vehicles.get(index).driver.getGender() == 'F');

        } else if (vehicleType == VehicleType.NORMAL) {

            do {
            
                index = ApplicationLibrary.rand(this.vehicles.size());
                
            } while (!this.vehicles.get(index).isFree());
        } else if (vehicleType == VehicleType.SHARED) {
            int dist = 1000000;
            for (int i = 0; i < this.vehicles.size(); ++i) {
                IVehicle vehicle = vehicles.get(i);
                if (!vehicle.isFree && vehicle.getService().vehicleType == VehicleType.SHARED)
                && ApplicationLibrary.distance(origin, vehicle.getLocation() <= 3
                && vehicle.getPassengers() == 1
                && vehicle.service_shared == null) {
                    index = i;
                    dist = ApplicationLibrary.distance(origin, vehicle.getLocation())
                }
            }

            if(index == -1) {
                findFreeVehicle(VehicleType.NORMAL);
            }            
        }
        
        
        return index;
    }

    private int findUserIndex(int id) {
        for (int i=0; i<this.users.size(); i++) {
            if (this.users.get(i).getId() == id)
                return i;
        }
        
        return -1;
    }
}