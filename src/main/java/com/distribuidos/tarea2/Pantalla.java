package com.distribuidos.tarea2;

import java.util.Scanner;
import java.io.IOException;
import com.distribuidos.tarea2.api.ServicioGrpc;
import com.distribuidos.tarea2.api.ServicioGrpc.ServicioBlockingStub;
import com.distribuidos.tarea2.api.ProtocoloAtt.ConsultaPistasTorre;
import com.distribuidos.tarea2.api.ProtocoloAtt.RespuestaPistasTorre;
import com.distribuidos.tarea2.api.ProtocoloAtt.ConsultaPistaAterrizaje;
import com.distribuidos.tarea2.api.ProtocoloAtt.RespuestaPistaAterrizaje;
import com.distribuidos.tarea2.api.ProtocoloAtt.ConsultaPistaDespegue;
import com.distribuidos.tarea2.api.ProtocoloAtt.RespuestaPistaDespegue;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import de.vandermeer.asciitable.AsciiTable;

public class Pantalla {

    public static void main( String[] args ) throws InterruptedException {
        Scanner user_input = new Scanner( System.in );
        System.out.print("Ingrese la direccion IP de la torre: ");
        String dir_ip = user_input.next();
        user_input.close();
        
        ManagedChannel channel = ManagedChannelBuilder.forAddress( dir_ip, 7777 ).usePlaintext().build();
        ServicioBlockingStub stub = ServicioGrpc.newBlockingStub( channel );

        ConsultaPistasTorre consultaCantPistas = ConsultaPistasTorre.newBuilder().build();
        RespuestaPistasTorre respuestaCantPistas = stub.pantallaInit(consultaCantPistas);

        int cantPistasAterrizaje = respuestaCantPistas.getPistasAterrizaje();
        int cantPistasDespegue = respuestaCantPistas.getPistasDespegue();
        String nombreTorre = respuestaCantPistas.getTorre();


        int idxPistaAterrizaje = 0;
        int idxPistaDespegue = 0;

        while(true) {
            clrscr();
            System.out.println("[Pantalla de informacion - "+nombreTorre+"]");
            AsciiTable at = new AsciiTable();
            at.addRule();
            at.addRow(null, null, "Departures", null, null, "Arrivals");
            at.addRule();
            at.addRow("Avion", "Destino", "Pista", "Avion", "Origen", "Pista");
            at.addRule();
            for(idxPistaAterrizaje = 0,idxPistaDespegue = 0; 
                            (idxPistaAterrizaje < cantPistasAterrizaje) || (idxPistaDespegue < cantPistasDespegue); 
                            idxPistaAterrizaje++, idxPistaDespegue++) {

                String[] aterrizaje = {null, null, " "};
                String[] despegue = {null, null, " "};
                if (idxPistaAterrizaje < cantPistasAterrizaje) {
                    ConsultaPistaAterrizaje consultaPistaAt = ConsultaPistaAterrizaje.newBuilder().setPista(idxPistaAterrizaje).build();
                    RespuestaPistaAterrizaje respuestaPistaAt = stub.pantallaAterrizaje(consultaPistaAt);
                    if(respuestaPistaAt.getVuelo() != "") {
                        aterrizaje[0] = respuestaPistaAt.getVuelo();
                        aterrizaje[1] = respuestaPistaAt.getOrigen();
                        aterrizaje[2] = String.valueOf(idxPistaAterrizaje);
                    }
                }
                if (idxPistaDespegue < cantPistasDespegue) {
                    ConsultaPistaDespegue consultaPistaDe = ConsultaPistaDespegue.newBuilder().setPista(idxPistaDespegue).build();
                    RespuestaPistaDespegue respuestaPistaDe = stub.pantallaDespegue(consultaPistaDe);
                    if(respuestaPistaDe.getVuelo() != "") {
                        despegue[0] = respuestaPistaDe.getVuelo();
                        despegue[1] = respuestaPistaDe.getDestino();
                        despegue[2] = String.valueOf(idxPistaDespegue);
                    }
                }
                at.addRow(despegue[0], despegue[1], despegue[2], aterrizaje[0], aterrizaje[1], aterrizaje[2]);
                at.addRule();
            }
            String rend = at.render();
            System.out.println(rend);
            Thread.sleep(1000);
        }
    }

    public static void clrscr(){
        //Clears Screen in java
        try {
            if (System.getProperty("os.name").contains("Windows"))
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            else
                Runtime.getRuntime().exec("clear");
        } catch (IOException | InterruptedException ex) {}
    }

}