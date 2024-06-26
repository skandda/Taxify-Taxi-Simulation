package taxify;

public interface IVehicle extends IMovable {

    public int getId();
    public ILocation getLocation();
    public ILocation getDestination();
    public IService getService();
    public IStatistics getStatistics();
    public void setCompany(ITaxiCompany company);
    public void pickService(IService service);
    public void startService();
    public void endService();
    public void notifyArrivalAtPickupLocation();
    public void notifyArrivalAtDropoffLocation();
    public boolean isFree();
    public String toString();
    public int getPassengers();
    public void addPassengers();
    public void updateRoute();
    public IService getSharedService();
    public IDriver getDriver();
	int calculateCost(boolean discount);
    
}
