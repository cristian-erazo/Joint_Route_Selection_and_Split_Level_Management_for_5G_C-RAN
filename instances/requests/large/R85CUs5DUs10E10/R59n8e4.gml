graph [
  node [
    id 0
    label "0"
    type 2
    prc 3
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
    prc 3
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
    type 1
    prc 5
    ant 2
    prb 6
    x 80
    y 100
  ]
  node [
    id 5
    label "5"
    type 1
    prc 3
    ant 7
    prb 5
    x 73
    y 99
  ]
  node [
    id 6
    label "6"
    type 1
    prc 4
    ant 9
    prb 4
    x 81
    y 114
  ]
  node [
    id 7
    label "7"
    type 1
    prc 3
    ant 2
    prb 5
    x 66
    y 93
  ]
  edge [
    source 0
    target 5
    bandwith 469
    delay 133
  ]
  edge [
    source 1
    target 6
    bandwith 938
    delay 389
  ]
  edge [
    source 2
    target 7
    bandwith 775
    delay 113
  ]
  edge [
    source 3
    target 4
    bandwith 395
    delay 495
  ]
]
