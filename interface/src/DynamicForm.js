import { useForm } from "react-hook-form";
import Button from '@mui/material/Button';
import Box from '@mui/material/Box';
import FormField from "./FormField";
import { solutionRequest } from "./APIUtils";

function DynamicForm(props) {
    const { control, handleSubmit } = useForm({});

    function inferSolution(data) {
        let reqData = { inputs: data }
        solutionRequest(reqData).then((response) => {
            let suggestions = response.map((suggestion, i) =>
                (<p style={{fontSize: "0.8em"}} key={i}>{suggestion.values.reduce((result, item) => (`${item}, ${result}`), "").slice(0, -2)} </p>))
            props.setSuggestion(suggestions)
        })
    }

    return (
        <form onSubmit={handleSubmit(data => inferSolution(data))}>
            {props.fields.map((fieldData) => (
                <FormField fieldData={fieldData} key={fieldData.name} control={control} />
            ))}
            <Box sx={{ m: 2 }} />
            <Button variant="contained" color="success" type="submit">Suggest</Button>
        </form>
    );
}

export default DynamicForm;