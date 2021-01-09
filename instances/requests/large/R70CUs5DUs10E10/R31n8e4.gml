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
    prc 2
  ]
  node [
    id 2
    label "2"
    type 2
    prc 5
  ]
  node [
    id 3
    label "3"
    type 2
    prc 2
  ]
  node [
    id 4
    label "4"
    type 1
    prc 2
    ant 10
    prb 5
    x 114
    y 108
  ]
  node [
    id 5
    label "5"
    type 1
    prc 2
    ant 8
    prb 2
    x 102
    y 110
  ]
  node [
    id 6
    label "6"
    type 1
    prc 4
    ant 8
    prb 4
    x 36
    y 67
  ]
  node [
    id 7
    label "7"
    type 1
    prc 1
    ant 2
    prb 2
    x 38
    y 114
  ]
  edge [
    source 0
    target 7
    bandwith 780
    delay 206
  ]
  edge [
    source 1
    target 6
    bandwith 320
    delay 135
  ]
  edge [
    source 2
    target 4
    bandwith 865
    delay 177
  ]
  edge [
    source 3
    target 5
    bandwith 610
    delay 226
  ]
]
