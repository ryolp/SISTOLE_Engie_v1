package enruta.sistole_gen;

import java.util.Vector;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import enruta.sistole_gen.entities.EmpleadoCplEntity;

public class Principal extends Fragment {

    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private View rootView;
    private TextView tv_resumen;
    private Main ma_papa;
    private Globales globales;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.entrada_principal, container, false);

        ma_papa = (Main) getActivity();
        globales = (Globales)rootView.getContext().getApplicationContext();

        actualizaResumen();
        llenarInfoLecturista();

        //Esto fue una prueba... es la manera de comunicarme con su padre...
        //((Main)getActivity()).finish();

        return rootView;
    }

    private void openDatabase() {
        dbHelper = new DBHelper(getActivity());

        db = dbHelper.getReadableDatabase();
    }

    private void closeDatabase() {
        db.close();
        dbHelper.close();

    }

    public void actualizaResumen() {


        String lote = "";
        String cpl = "";
        String mac_bt = "";
        String mac_impr = "";


        String ls_resumen;

        tv_resumen = (TextView) rootView.findViewById(R.id.tv_resumen);

        Cursor c;
        openDatabase();

//		    		try{
//		    			c=db.rawQuery("Select value from config where key='cpl'", null);
//		        		c.moveToFirst();
//		        		cpl=c.getString(c.getColumnIndex("value"));
//		    		}
//		    		catch(Throwable e){
//		    			
//		    		}
//		    		
//		    		try{
//		    			c=db.rawQuery("Select value from config where key='lote'", null);
//		        		c.moveToFirst();
//		        		lote=c.getString(c.getColumnIndex("value"));
//		    		}
//		    		catch(Throwable e){
//		    			
//		    		}
//		        		
//		        		try{
//		        			c=db.rawQuery("Select value from config where key='mac_bluetooth'", null);
//			        		c.moveToFirst();
//			        		mac_bt=c.getString(c.getColumnIndex("value"));
//		        		}catch(Throwable e){
//		        			
//		        		}
//		        		
//		        		
//		        		try{
//		        			c=db.rawQuery("Select value from config where key='mac_impresora'", null);
//			        		c.moveToFirst();
//			        		mac_impr=c.getString(c.getColumnIndex("value"));
//		        		}catch(Throwable e){
//		        			
//		        		}
//		        		
//		        		
//		    		
//		    		
//		    		
//		        	//ll_restantes = ll_total-ll_tomadas ;
//		    		Vector <EstructuraResumen>resumen= new Vector<EstructuraResumen>();
//		    		
//		    		resumen= ma_papa.globales.tdlg.getResumen(db);
//		    		
//		    		resumen.add(new EstructuraResumen(cpl, getString(R.string.info_CPL)));
//		    		resumen.add(new EstructuraResumen(lote,getString(R.string.info_lote)));
//		        	
////		        	ls_resumen="CPL: " + cpl +"\n" +
////		        			"Lote: " +  lote +"\n";
//		        	
//		        	if (!mac_bt.equals("") && !mac_bt.equals(".")){
//		        		   
////		        			ls_resumen+="MAC BT: \n"+ mac_bt +"\n";
//		        		resumen.add(new EstructuraResumen(mac_bt, /*getString(R.string.info_macBluetooth)*/"MAC"));
//		        	}
//		        	
//		        	if (!mac_impr.equals("") && !mac_impr.equals(".")){
//		        		   
////	        			ls_resumen+="\nMAC Impr: \n"+ mac_impr;
//		        		resumen.add(new EstructuraResumen( mac_impr, getString(R.string.info_macImpresora)));
//	        	}
//		        	
//		        	if (!ma_papa.globales.getUsuario().equals("")){
//		        		
//		        		if (!ma_papa.globales.mostrarCodigoUsuario)
//		        			resumen.add(new EstructuraResumen( ma_papa.is_nombre_Lect, "Lect."));
//		        		else
//		        			resumen.add(new EstructuraResumen( ma_papa.globales.getUsuario(), "Lect."));
//		        	}
//		        	
////		        	tv_resumen.setText(ls_resumen);
//		        	
//		        	//Establecemos el adaptador
        GridView gv_resumen = (GridView) rootView.findViewById(R.id.gv_resumen);

        Vector<EstructuraResumen> resumen = ma_papa.globales.tdlg.getPrincipal(db);


        gv_resumen.setAdapter(new ResumenGridAdapter(getActivity(), resumen, ma_papa.infoFontSize * ma_papa.porcentaje));

        tv_resumen.setVisibility(View.GONE);
        gv_resumen.setVisibility(View.VISIBLE);
        closeDatabase();


    }

    private void llenarInfoLecturista() {
        TextView lblLecturista;
        TextView lblActivoDesde;
        TextView lblCelular;
        EmpleadoCplEntity emp;

        lblLecturista = (TextView)rootView.findViewById(R.id.lbl_lecturista);
        lblActivoDesde = (TextView)rootView.findViewById(R.id.lbl_activoDesde);
        lblCelular = (TextView)rootView.findViewById(R.id.lbl_celular);

        if (lblLecturista == null || lblActivoDesde == null || lblCelular == null) {
            showMessageLong("Error al crear controles");
            return;
        }

        if (globales.usuarioEntity == null)
            return;

        lblLecturista.setText(globales.usuarioEntity.NombreCompleto);
        //lblActivoDesde.setText(globales.usuarioEntity.FechaActivo);
        lblCelular.setText(globales.usuarioEntity.Telefono);
    }

    private void showMessageLong(String sMessage) {
        Toast.makeText(rootView.getContext(), sMessage, Toast.LENGTH_LONG).show();
    }

    private void showMessageShort(String sMessage) {
        Toast.makeText(rootView.getContext(), sMessage, Toast.LENGTH_SHORT).show();
    }
}
