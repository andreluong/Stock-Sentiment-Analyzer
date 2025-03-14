import { Input } from "@heroui/input";
import { Icon } from "@iconify/react/dist/iconify.js";
import axios from "axios";
import { useState } from "react";
import { Button } from "@heroui/react";
import PopularStocksGrid from "./components/PopularStocksGrid";
import Searches from "./components/Searches";
import Trending from "./components/Trending";
import { StockAnalysis } from "./types";
import Hero from "./components/Hero";
import Navbar from "./components/Navbar";
import Footer from "./components/Footer";

const View = {
  popular: {
    name: "✨ Popular",
    value: 1
  },
  searches: {
    name: "📖 Searches",
    value: 2
  },
  trending: {
    name: "🔥 Trending",
    value: 3
  }
};

function App() {
  const [isInvalidStockSymbol, setIsInvalidStockSymbol] = useState(false);
  const [stockSymbol, setStockSymbol] = useState('');
  const [currentView, setCurrentView] = useState<number>(View.popular.value); // Default to Searches
  const [isLoading, setIsLoading] = useState(false);

  const handleKeyPress = async (event: React.KeyboardEvent<HTMLInputElement>) => {
    if (event.key !== "Enter") return;

    if (stockSymbol === "") {
      setIsInvalidStockSymbol(true);
      return;
    }

    console.log("Searching: " + stockSymbol);
    setIsLoading(true);
    
    await axios
      .post(`http://localhost:8080/api/stocks/analyze?symbol=${stockSymbol}`)
      .then((response) => {
        // Process article list
        const data = response.data as StockAnalysis;

        if (data.stock === undefined || data.articleList === undefined) {
          setIsInvalidStockSymbol(true);
        } else {
          setIsInvalidStockSymbol(false);
          console.log(data);
          sessionStorage.setItem("stock:" + stockSymbol, JSON.stringify(data));
        }
        setIsLoading(false);    
      })
      .catch((error) => {
        console.error(error.message);
      });

    console.log("Done searching: " + stockSymbol);
    stockSymbol === "";
  };

  return (
    <>
      <Navbar /> 
      <Hero />
      
      {/* Main Content */}
      <div className="w-3/5 mx-auto">
        {/* Toolbar */}
        <div className="flex flex-row mx-auto space-x-2 py-4">
          <Input
            className="border-2 rounded-md mr-8 hover:border-black"
            variant="faded"
            placeholder="Symbol"
            startContent={<Icon icon="material-symbols:search-rounded" width="24" height="24" />}
            maxLength={5}
            value={stockSymbol}
            errorMessage={isInvalidStockSymbol ? "Invalid stock symbol" : undefined}
            onValueChange={(value) => setStockSymbol(value.toUpperCase())}
            onKeyPress={handleKeyPress}
            isClearable
          />

          {/* Buttons */}
          {Object.entries(View).map(([key, { name, value }]) => (
            <div key={key} className="h-full my-auto">
              <Button className="border-2 rounded-md hover-shadow" onPress={() => setCurrentView(value)}>
                {name}
              </Button>
            </div>
          ))}
        </div>

        {currentView === View.searches.value && (
          <Searches isLoading={isLoading} />
        )}

        {currentView === View.popular.value && (
          <PopularStocksGrid />
        )}

        {currentView === View.trending.value && (
          <Trending />
        )}
      </div>

      <Footer />
    </>
  )
}

export default App
