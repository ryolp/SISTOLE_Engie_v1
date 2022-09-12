package enruta.sistole_gen;

import java.io.IOException;
import java.net.URL;
import java.util.Vector;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
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
    private ImageView fotoEmpleado = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.entrada_principal, container, false);

        ma_papa = (Main) getActivity();
        globales = (Globales) rootView.getContext().getApplicationContext();

        actualizaResumen();

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

        mostrarFoto();
    }

    protected void mostrarFoto() {
        if (globales == null)
            return;

        if (globales.usuarioEntity == null) {
            return;
        }

        if (globales.usuarioEntity.FotoURL.trim().equals(""))
            return;

        if (fotoEmpleado == null)
            fotoEmpleado = rootView.findViewById(R.id.fotoEmpleado);

        if (globales.usuarioEntity.fotoEmpleado == null){
            AsyncTaskRunner runner = new AsyncTaskRunner();

            runner.execute(globales.usuarioEntity.FotoURL);
        }
        else
            fotoEmpleado.setImageBitmap(globales.usuarioEntity.fotoEmpleado);
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private Bitmap bmp;

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(globales.usuarioEntity.FotoURL);
                globales.usuarioEntity.fotoEmpleado = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                fotoEmpleado.setImageBitmap(globales.usuarioEntity.fotoEmpleado);
            } catch (Exception e){
                String msg;

                msg = e.getMessage();
            }
            return "";
        }
    }
}
