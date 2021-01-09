graph [
  node [
    id 0
    label "0"
    type 2
    prc 2
  ]
  node [
    id 1
    label "1"
    type 2
    prc 3
  ]
  node [
    id 2
    label "2"
    type 2
    prc 4
  ]
  node [
    id 3
    label "3"
    type 2
    prc 5
  ]
  node [
    id 4
    label "4"
    type 2
    prc 3
  ]
  node [
    id 5
    label "5"
    type 1
    prc 1
    ant 6
    prb 2
    x 30
    y 30
  ]
  node [
    id 6
    label "6"
    type 1
    prc 5
    ant 9
    prb 5
    x 87
    y 84
  ]
  node [
    id 7
    label "7"
    type 1
    prc 1
    ant 6
    prb 6
    x 112
    y 24
  ]
  node [
    id 8
    label "8"
    type 1
    prc 1
    ant 2
    prb 3
    x 88
    y 19
  ]
  node [
    id 9
    label "9"
    type 1
    prc 5
    ant 2
    prb 3
    x 23
    y 31
  ]
  node [
    id 10
    label "10"
    type 1
    prc 3
    ant 3
    prb 6
    x 120
    y 85
  ]
  edge [
    source 0
    target 5
    bandwith 693
    delay 108
  ]
  edge [
    source 1
    target 7
    bandwith 785
    delay 363
  ]
  edge [
    source 2
    target 10
    bandwith 220
    delay 390
  ]
  edge [
    source 3
    target 6
    bandwith 172
    delay 304
  ]
  edge [
    source 4
    target 8
    bandwith 730
    delay 284
  ]
  edge [
    source 4
    target 9
    bandwith 463
    delay 335
  ]
]
