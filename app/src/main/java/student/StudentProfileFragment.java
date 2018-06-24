package student;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.com.traineeshare.R;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class StudentProfileFragment extends Fragment {

    private Spinner selection_spinner;

    public StudentProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_student_profile, container, false);

        selection_spinner = rootView.findViewById(R.id.spinner_profile_selection);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.studentProfileSelection, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selection_spinner.setAdapter(adapter);
        selection_spinner.setOnItemSelectedListener(listener);

        return rootView;
    }

    AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {
            Spinner spinner = (Spinner) parent;
            switch (spinner.getSelectedItemPosition()) {
                case 0:
                    fragmentControl(Student_Profile_Preview_Fragment.class);
                    break;
                default:
                    break;
            }

        }

        public void onNothingSelected(AdapterView<?> parent) {
            Toast.makeText(getActivity(), "Nothing", Toast.LENGTH_SHORT).show();
        }
    };

    public void fragmentControl(Class fragmentClass) {
        Fragment fragment = null;
        Class fragmentCls = fragmentClass;

        try {
            fragment = (Fragment) fragmentCls.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.studentProfile_flContent, fragment).commit();
    }
}
