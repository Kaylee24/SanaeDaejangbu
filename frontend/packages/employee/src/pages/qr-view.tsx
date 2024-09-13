import { v4 as uuidv4 } from 'uuid' // UUID 생성 함수
import QRCode from 'react-qr-code' // QR 코드 렌더링 라이브러리, QR 코드는 value 속성에 따라 생성
import { useState, useEffect } from 'react'
import { useEmployeeMeal, useValidationId } from '@/hooks/api'

import { Iconify } from '@e201/ui' // React | 생명주기 관리, 상태 관리
import { useBoolean } from '@/hooks/use-boolean'

import { Box, Typography } from '@mui/material'

export default function QrPage() {
  const [qrData, setQrData] = useState<string | null>(null)
  const { value: isExpired, onTrue, onFalse } = useBoolean(false) // QR 코드 만료 여부
  const [timeLeft, setTimeLeft] = useState(90) // QR 코드 타이머 (90초)

  const id = 'e7ce2fe0-bf18-408e-b3ea-cb45fa46a469'
  const { data: meal, isLoading, error } = useEmployeeMeal(id)

  // 데이터를 받아온 후 식대 사용 현황 계산
  const totalAmount = meal?.monthlyAmount ?? 0
  const currentUsage = meal?.currentUsage ?? 0
  const remainingAmount = totalAmount - currentUsage

  // useValidationId 훅을 사용하여 POST 요청 처리
  const mutation = useValidationId()

  // QR 코드를 생성하고 POST 요청을 보내는 함수 (새로고침 시 재사용)
  const generateQrCode = () => {
    const validationId = uuidv4() // 1. UUID 생성

    // qrData 는 UUID 가 생성된 후에만 설정
    const newQrData = JSON.stringify({
      userId: '0000',
      validationId,
    })
    setQrData(newQrData)
    onFalse() // QR 코드 만료 상태 초기화
    setTimeLeft(90) // QR 코드 타이머 초기화

    // 서버로 데이터 전송
    mutation.mutate({ generatedId: validationId }) // 서버가 받는 변수명 generatedId 로 변경
  }

  // 처음 마운트 시 QR 코드 생성
  useEffect(() => {
    generateQrCode() // QR 코드 최초 생성
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  //
  useEffect(() => {
    if (timeLeft > 0) {
      const timer = setTimeout(() => setTimeLeft(timeLeft - 1), 1000)
      return () => clearTimeout(timer)
    }
    onTrue() // QR 코드 만료 상태로 전환

    // 빈 함수 반환
    return () => {}
  }, [timeLeft, onTrue])

  // 남은 시간을 분:초 형식으로 변환하는 함수
  const formatTime = (time: number) => {
    const minutes = Math.floor(time / 60)
    const seconds = time % 60
    return `${minutes}:${seconds < 10 ? '0' : ''}${seconds}` // 초가 한 자리일 경우 앞에 0 추가
  }

  // 만료 시 새로고침 아이콘을 클릭하여 QR 코드를 새로 생성하는 함수
  const handleRefreshClick = () => {
    generateQrCode() // QR 코드 재발급
  }

  if (isLoading) return <Typography variant="h5">Loading...</Typography>
  if (error) return <Typography variant="h5">Error: {error.message}</Typography>

  return (
    <Box textAlign="center">
      <h2>정산하기</h2>

      {/* 남은 시간 표시 */}
      {/* {!isExpired && <p>{formatTime(timeLeft)}</p>} */}
      <p>{formatTime(timeLeft)}</p>

      <Box
        sx={{
          margin: '16px 0',
          position: 'relative',
          display: 'inline-block',
          cursor: isExpired ? 'pointer' : 'default',
        }}
        role="button" // 이 Box가 버튼 역할을 한다는 것을 명시
        tabIndex={0} // 키보드 포커스를 받을 수 있게 함
        onClick={isExpired ? handleRefreshClick : undefined}
        onKeyDown={(e) => {
          if (e.key === 'Enter' || e.key === ' ') {
            handleRefreshClick()
          }
        }}
        aria-label="Refresh QR code"
      >
        {/* QR 코드 */}
        <Box style={{ filter: isExpired ? 'blur(5px)' : 'none' }}>
          {qrData && <QRCode value={qrData} size={200} />}
        </Box>

        {/* 2분 타이머가 종료되면 새로고침 아이콘 표시 */}
        {isExpired && (
          <Box
            sx={{
              position: 'absolute',
              top: '50%',
              left: '50%',
              transform: 'translate(-50%, -50%)',
              backgroundColor: 'white',
              width: '60px', // 정사각형 크기 지정
              height: '60px',
              borderRadius: '30px',
              boxShadow: '0px 4px 6px rgba(0, 0, 0, 0.1)',
              display: 'flex', // 아이콘을 가운데 정렬하기 위해 flex 사용
              justifyContent: 'center',
              alignItems: 'center',
            }}
          >
            <Iconify icon="mdi:autorenew" width="48" height="48" />
          </Box>
        )}
      </Box>

      <Box>
        {/* 결제 금액 정보 */}
        <Typography variant="h5">
          {currentUsage.toLocaleString()}원 / {totalAmount.toLocaleString()}원
        </Typography>
        <Typography color="#1976d2" fontWeight="bold">
          남은 금액 : {remainingAmount.toLocaleString()}원
        </Typography>
      </Box>
    </Box>
  )
}
