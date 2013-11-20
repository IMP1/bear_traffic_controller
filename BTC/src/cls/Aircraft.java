package cls;

import lib.jog.graphics;
import lib.jog.input;

import scn.Demo;

public class Aircraft {

	public final static int separationRule = 1000;
	
	private double _speed;
	
	private Vector _position;
	private Vector _velocity;
	private boolean _manualControl;
	private String _flightName;
	private Vector _target;
	private String _originName, _destinationName;
	private Waypoint[] _route;
	private int _routeStage;
	private cls.Vector _destination;
	private graphics.Image _image;
	private boolean _finished;

	//Constructor
	public Aircraft(String flightName, String originName, String destinationName, cls.Vector origin, cls.Vector destination, graphics.Image image, double speed) {
		_flightName = flightName;
		_destinationName = destinationName;
		_originName = originName;
		_route = findRoute(origin, destination);
		_target = _route[0].position();
		_image = image;
		_speed = speed;
		_position = origin;
		_destination = destination;
		_manualControl = false;
		double x = _target.x() - _position.x();
		double y = _target.y() - _position.y();
		_velocity = new Vector(x, y, 0).normalise().mul(speed);
		_finished = false;
		_routeStage = 0;
		
		System.out.println("New Flight Created");
		System.out.println("------------------");
		System.out.println("Origin:\t\t\t" + _originName + " (" + _position.x() + ", " + _position.y() + ")");
		System.out.println("Destination:\t\t" + _destinationName + " (" + _destination.x() + ", " + _destination.y() + ")");
		System.out.println("Flight Path:\t\t(" + _target.x() + ", " + _target.y() + "),");
		for (int i = 1; i < _route.length; i ++) {
			System.out.println("\t\t\t(" + _route[i].position().x() + ", " + _route[i].position().y() + "),");
		}
	}
	
	public Vector position() {
		return _position;
	}
	
	public String name () {
		return _flightName;
	}
	
	public boolean isFinished() {
		return _finished;
	}
	
	public boolean isMouseOver(int mx, int my) {
		double dx = _position.x() - mx;
		double dy = _position.y() - my;
		return dx*dx + dy*dy < 64;
	}
	public boolean isMouseOver() { return isMouseOver(input.mouseX(), input.mouseY()); }
	
	public void update(double dt) {
		if (_finished) return;

		cls.Vector dv = _velocity.mul(dt);
		_position = _position.add(dv);
		
		Vector oldTarget = _target;
		if (isAt(_target) && _target.equals(_destination)) {
			_finished = true;
		} else if (isAt(_target) && _target.equals(_route[_route.length-1].position())) {
			_routeStage ++;
			_target = _destination;
		} else if (isAt(_target)) {
			_routeStage ++;
			_target = _route[_routeStage].position();
		}
		if (oldTarget != _target) {
			turnTowards(_target.x(), _target.y());
		}
	}
	
	private void turnTowards(double tx, double ty) {
		double x = tx - _position.x();
		double y = ty - _position.y();
		_velocity = new Vector(x, y, 0).normalise().mul(_speed);
	}
	
	public void draw() {
		graphics.draw(_image, _position.x(), _position.y(), bearing(), 8, 8);
	}
	
	public double bearing() {
		double a = Math.acos( _velocity.x() / Math.sqrt(Math.pow(_velocity.x(), 2) + Math.pow(_velocity.y(), 2)) );
		if (_velocity.x() < 0) a *= -1;
		if (_velocity.y() < 0) a *= -1;
		return a;
	}
	
	public boolean isAt(Vector point) {
		double dy = point.y() - _position.y();
		double dx = point.x() - _position.x();
		return dy*dy + dx*dx < 16;
	}
	
	private Waypoint[] findRoute(cls.Vector origin, cls.Vector destination) {
		// Placeholder over-simplified version
		int n = 4;
		Waypoint[] route = new Waypoint[n];
		for (int i = 0; i < n; i ++ ){
			route[i] = Demo._waypoints[(int)( Math.random() * Demo._waypoints.length )];
			System.out.println(route[i].position().x() + ", " + route[i].position().y());
		}
		return route;
	}
}