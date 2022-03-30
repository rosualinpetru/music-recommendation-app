import Autocomplete from '@mui/material/Autocomplete';
import TextField from '@mui/material/TextField';
import Box from '@mui/material/Box';
import { Controller } from "react-hook-form";

function FormField(props) {
    return (
        <Box sx={{ m: 1.5 }} >
            <Controller
                render={({ field: { onChange } }) =>
                    <Autocomplete
                        id={props.fieldData.name}
                        onChange={(_, data) => {
                            onChange(data);
                            return data;
                        }}
                        options={props.fieldData.values}
                        sx={{ width: 350, m: "auto" }}
                        renderInput={(params) => <TextField {...params} label={props.fieldData.question} />}
                    />}
                name={props.fieldData.name}
                control={props.control}
            />
        </Box>
    );
}

export default FormField;