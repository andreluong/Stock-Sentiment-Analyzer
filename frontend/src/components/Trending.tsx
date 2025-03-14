import { useState } from 'react'

export default function Searches() {
    const [isLoading, setIsLoading] = useState(false);
    const [stocks, setStocks] = useState([]);

  return (
        <div className='h-screen'>
            {isLoading && <div>Loading...</div>}
            {!isLoading && (
                <div>
                    {stocks.length == 0 && <p className='m-20'>No trending stocks found</p>}
                </div>
            )}
        </div>
  )
}
