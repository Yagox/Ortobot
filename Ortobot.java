package obot;

import java.awt.Color;

import robocode.AdvancedRobot;
import robocode.HitByBulletEvent;
import robocode.ScannedRobotEvent;
import robocode.WinEvent;
import robocode.util.Utils;


/**
 * @author      Yago F.R.
 * @version     1.0
 * @objetive 	Robot creado para el aprendizaje y diversion de Robocode, se basa en escanear un robot,
 * 				situarse en una esquina y trazar cuadrados de movimiento en ella, movimiento en angulos rectos (ortogonal).
 */

public class Ortobot extends AdvancedRobot {
	
	// Variables globales.
	private boolean esquina = false; //Variable para indicar si se esta en una esquina.
	private boolean movimiento = false; //Variable por si se necesita realizar movimiento o girar.
	private int direccion = 1; //Variable para indicar la direccion a mover.
	private int contador = 0; //Variable para el contador de actuacion.
	private short energiaObjAnt; //Variable donde se guarda la energia del objetivo.
	private String objetivo; //Variable para el nombre del objetivo.
	private double intensidadAlta = 3; //Variable para indicar la potencia maxima de disparo.
	private double intensidadBaja = 2; //Variable para indicar la potencia minima de disparo.
	private int distanciaMinima = 250; //Variable para indicar los pixeles minimos de distancia para la potencia de fuego.
	private int maxContador = 6; //Variable con la cantidad maxima a esperar por el contador para actuar.
	private int tamMovimiento = 180; //Variable para indicar la cantidad de movimiento.
	private int giro = 90; //Variable que indica los grados de giro. 
	private boolean victoria = false; //Variable que indica si hemos ganado o no la ronda.
	
	/**
	 * Método donde se inicialia el robot y se ejecuta el giro continuo del radar.
	 */
	@Override
	public void run() {
		setColors(Color.BLACK, Color.RED, Color.RED, Color.YELLOW, Color.GREEN); //Seleccionar el color del tanke (cuerpo, cañon, bala, radar, arco).
		setAdjustGunForRobotTurn(true); //Configuracion para que el cañon gire independientemente del cuerpo.
		setAdjustRadarForGunTurn(true); //Configuracion para que el radar gire independientemente del cuerpo.
		
		while (true) { //Bucle infinito para fijar blanco con el radar, no para de mover el radar para mantener el objetivo fijado.
			if (!victoria){
			turnRadarRightRadians(1); //Gira continuamente el radar a la iquierda.
			}else{
				//Baile de la victoria.
				for (int i = 0; i < 10; i++) {
					setAhead(20);
					setTurnGunRight(20);
					setBack(20);
					setTurnGunLeft(20);
				}
				 victoria = false;
			}
		}
	}

	/**
	 * Método actuador cuando se recibe un balazo.
	 */	
	@Override
	public void onHitByBullet(HitByBulletEvent e) {
		objetivo = e.getName(); //Se guarda el nombre del atacante,
	}

	/**
	 * Método actuador cuando se detecta un robot.
	 */	
	@Override
	public void onScannedRobot(ScannedRobotEvent e) {
		if (objetivo == null || contador > maxContador) { //Caso en que no tenemos objetivo o el contador sea mayor del maximo.
			objetivo = e.getName(); //Guardamos el nombre del primer robot escaneado.
		}

		if (getTurnRemaining() == 0 && getDistanceRemaining() == 0) { //Si no nos quedan turnos o distancia por recorrer (para ir alternando los turnos)
			if (esquina) {//Si nos situamos en una esquina.
				
				
				if (movimiento) { //Si tenemos movimiento o estabamos haciendolo.
					setTurnLeft(giro); //Giramos a la izquierdael cuerpo 90º.
					movimiento = false; //Paramos el movimiento.
				} else { //Si no tenemos movimiento o no nos tocaba mover.
					setAhead(tamMovimiento * direccion); //Mover * direccion para indicar el sentido.
					movimiento = true; //Indicamos que hay movimiento.
				}
				
				
			} else { //Si no estamos en una esquina.

				//Algoritmia para realizar el movimiento cuadrado.
				//Arriba 0º
				//Derecha 90º
				//Izquierda 270º
				//Abajo 180º
				
	
				
				//Si no vamos arriba o abajo, se le hace el modulo al angulo donde miramos para saber nuestra posicion. 
				if ((getHeading() % 90) != 0) {
					
					if(getY() > (getBattleFieldHeight() / 2)){
						setTurnLeft(getHeading());
						
					}else{
						setTurnLeft(getHeading() - 180);
					}
					
				}
				
				// if we aren't at the top or bottom, go to whichever is closer
				//Si no estamos arriba o abajo, vamos a la zona superior o inferior mas cercana.
				else if (getY() > 30 && getY() < getBattleFieldHeight() - 30) {
					
					if(getHeading() > 90){
						setAhead(getY() - 20);
					}else{
						setAhead(getBattleFieldHeight() - getY() - 20);
					}
				}
				
				//Si no estamos mirando hacia la derecha o la izquierda, giramos.
				else if (getHeading() != 90 && getHeading() != 270) { //Origen de coordenadas arriba = 0 = norte.
					
					if (getX() < 350) {
						
						if(getY() > 300){
							setTurnLeft(90);
						}else{
							setTurnLeft(-90);
						}

					} else {
						
						if(getY() > 300){
							setTurnLeft(-90);
						}else{
							setTurnLeft(90);
						}
					
					}
					
				}
				
				//Si no estamos a la izquierda o derecha vamos a la zona de mas cercana a estas.
				else if (getX() > 30 && getX() < getBattleFieldWidth() - 30) {
					
					
					if(getHeading() < 180){
						setAhead(getX() - 20);
					}else{
						setAhead(getBattleFieldWidth() - getX() - 20);
					}
					
					
				}
				
				//Si estamos en la esquina izquierda, giramos y comenzamos a movernos.
				else if (getHeading() == 270) {
					
					if(getY() > 200){
						setTurnLeft(90);
					}else{
						setTurnLeft(180);
					}
					
					esquina = true;
				}
				
				//Si estamos en la esquina derecha; giramos y comenzamos a movernos.
				else if (getHeading() == 90) {
					
					if(getY() > 200){
						setTurnLeft(180);
					}else{
						setTurnLeft(90);
					}
					
					esquina = true;
				}
			}
		}
		
		if (e.getName().equals(objetivo)) { //Si el robot escaneado es nuestro objetivo.
			contador = 0; //Reseteamos el radar y el contador de actuacion.


			//Si algun enemigo nos dispara, con un 25% de probabilidad cambiaremos la direccion del cuerpo.
			//Detectamos que dispara al cambiarle la energia, ya que puede ser que haya disparado o recibido daño.
			if ((energiaObjAnt < (energiaObjAnt = (short) e.getEnergy())) && Math.random() > .75) {
				direccion = (direccion * -1); //Cambiamos la direccion, inversa de la que tuviesemos.
			}

			
			setTurnGunRightRadians(Utils.normalRelativeAngle(
					(e.getBearingRadians() + getHeadingRadians()) - getGunHeadingRadians())); //Movemos el cañon hacia el objetivo.
			
			
			if (e.getDistance() < distanciaMinima) { //Si el objetivo esta mas cerca de una distancia minima.
				setFire(intensidadAlta); //Disparar con intensidad alta.
				
			} else { //Si el objetivo esta a mas distancia, dispararemos mas flojo  para ahorrar ya que es menos probable acertar.
				setFire(intensidadBaja); //Disparar con intensidad baja.
			}

			//Calculamos la posicion del radar.
			double radarTurn = (e.getBearingRadians() + getHeadingRadians()) - getRadarHeadingRadians() ;
			
			//Fijamos el radar.
			setTurnRadarRightRadians(2 * Utils.normalRelativeAngle(radarTurn)); 
			
		} else if (objetivo != null) { //Si no tenemos objetivo enfocado.
			contador++; //Añadimos una unidad al contador para saber si actuar o no.
		}
	}//Fin de onScannedRobot.
	
	/**
	 * Método para hacer el baile de la victoria.
	 */
	public void onWin(WinEvent e) {
		victoria = true;
	}

}//Fin de la clase
