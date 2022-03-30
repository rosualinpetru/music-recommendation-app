import './App.css';
import { useState, useEffect } from "react";
import { toast } from "react-toastify";
import { interfaceRequest } from "./APIUtils";
import Loader from "./Loader";
import Grid from '@mui/material/Grid';
import Box from '@mui/material/Box';
import DynamicForm from "./DynamicForm";
import Button from '@mui/material/Button';

function App() {
  const [suggestion, setSuggestion] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [interfaceData, setInterfaceData] = useState([]);

  useEffect(() => {
    interfaceRequest()
      .then((data) => {
        setTimeout(() => {
          setInterfaceData(data);
          setIsLoading(false);
        }, 700);
      })
      .catch(() => toast.error("There is an error loading the interface!"));
  }, []);

  if (isLoading) {
    return (
      <div className="App">
        <header className="App-header">
          <Loader />
        </header>
      </div>)
  }

  let body
  if (suggestion != null) {
    body = (<div>
      <Box sx={{ m: 2 }} >
        <h4>Suggestions: </h4>
        {suggestion}
      </Box>
      <Box sx={{ m: 10 }} />
      <Button variant="contained" color="success" onClick={() => window.location.reload(false)}>Reset</Button>
    </div>
    )
  } else {
    body = (
      <div>
        <h3>{interfaceData.systemDescription}</h3>
        <Box sx={{ m: 2 }} />
        <DynamicForm fields={interfaceData.inputFields} setSuggestion={setSuggestion} />
      </div>
    )
  }

  return (
    <div className="App">
      <header className="App-header">
        <Grid
          container
          spacing={0}
          direction="column"
          alignItems="center"
          justifyContent="center"
          style={{ minHeight: '100vh' }}
        >
          <h1>{interfaceData.systemName}</h1>
          {body}

        </Grid>
      </header>
    </div>
  );
}

export default App;
